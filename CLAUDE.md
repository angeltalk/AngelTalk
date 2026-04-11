# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status

AngelTalk Plus is an **archived** Android AAC (Augmentative and Alternative Communication) app for children with complex communication needs. The original release stack (AGP 3, support libs, Dagger 2.20, Butterknife, Firebase 10) has been progressively modernized in `phase-*` migrations so the app builds against current toolchains. Current state: AGP 8.11.1 / Gradle 9.0.0 / Java 17, `compileSdk 35`, `targetSdk 35`, `minSdk 23`, Java-only sources, AndroidX, Hilt 2.52, Firebase BoM 33.1.2, Glide 4.16, Lombok 1.18.42. Butterknife has been removed — view binding uses `findViewById` + `setOnClickListener` (look for `TODO(phase-6)` markers near the call sites). Some migrations are still in flight: ACRA crash reporting and the Kakao SDK v2 share path are stubbed (`TODO(phase-8/9)`), and most legacy unit + instrumentation tests are excluded from the build pending Hilt-test ports.

## Build & Test Commands

The project uses the Gradle wrapper. The single `app` module is the Android application (`applicationId angeltalk.plus`).

- Full CI build (what the legacy Travis script runs): `sh script/build.sh` — equivalent to `./gradlew clean build coverageReport`
- Assemble debug APK: `./gradlew :app:assembleDebug`
- Assemble release APK: `./gradlew :app:assembleRelease` (uses ProGuard; debug is signed via `angeltalk.jks`)
- Compile-only sanity check: `./gradlew :app:compileDebugJavaWithJavac` — fastest way to verify a Java edit before running tests
- Unit tests (Robolectric + JUnit4 + Mockito + Hilt test): `./gradlew :app:testDebugUnitTest`
- Single unit test class: `./gradlew :app:testDebugUnitTest --tests "angeltalk.plus.some.pkg.SomeTest"`
- Single test method: `./gradlew :app:testDebugUnitTest --tests "angeltalk.plus.some.pkg.SomeTest.methodName"`
- Instrumentation tests (Espresso + UiAutomator): `./gradlew :app:connectedDebugAndroidTest` (requires a connected device/emulator — see Test Layout for the locale/animation setup the suite assumes)
- Run a single instrumentation test class: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=angeltalk.plus.presentation.activity.v1.MakeNewCategoryTest`
- Run all tests in a package: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=angeltalk.plus.presentation.activity.v1`
- Jacoco coverage report: `./gradlew :app:coverageReport` — outputs to `app/build/reports/jacoco/`
- SonarQube scan: `./gradlew sonarqube` (config in `app/sonarqube.gradle`)
- Clean: `./gradlew clean`

Note: unit tests fork every 10 tests with `-Xmx2g`. `unitTests.returnDefaultValues = true` is enabled, so un-stubbed Android framework calls return defaults rather than throwing.

## Architecture

The package root is `angeltalk.plus` under `app/src/main/java/`. The codebase follows a loose Clean Architecture split with Hilt providing application-scoped DI.

- **`presentation/`** — Android UI layer: `activity/`, `fragment/`, `adapter/`, `custom/` views, plus `manager/` (e.g. `ApplicationManager`, `ApplicationInitializer`, `NotificationActionManager`) and `service/` + `receiver/` for the lock-screen behavior that is the app's signature feature. The lock-screen card display and notification bar `OFF` toggle live here — be careful when touching services/receivers, since the app intentionally takes over lock-screen behavior.
- **`domain/`** — `model/` VOs (using Lombok for boilerplate) and `repository/` interfaces (`CardRepository`, `CategoryRepository`). This layer has no Android dependencies.
- **`data/`** — concrete repository implementations (`CardDataRepository`, `CategoryDataRepository`) backed by `sqlite/` (local DB via `DatabaseHelper`) and `repository/datastore/`. Data layer implements the domain interfaces.
- **`network/`** — Firebase + Retrofit clients. `transfer/` holds upload/download helpers (`CardTransfer`, `MessageTransfer`, `KaKaoTransfer`) that move card assets (images/video/audio) to Firebase Storage and share links via KakaoLink. `service/` holds Retrofit API interfaces (e.g. URL shortener).
- **`dagger/modules/AngelmanModule`** — Hilt `@Module @InstallIn(SingletonComponent.class)` that `@Binds` `CardRepository` → `CardDataRepository` and `CategoryRepository` → `CategoryDataRepository`. The legacy hand-written `AngelmanComponent` was deleted as part of the Hilt migration; everything else is `@Inject`-constructed. The `dagger/components/` directory is empty and kept only because git tracks it.

Cross-cutting conventions worth knowing before editing:

- **Hilt 2.52** — DI is wired via `@HiltAndroidApp` on `AngelmanApplication`, with `@AndroidEntryPoint` on activities/fragments/services/receivers/custom views and `@Inject` on constructors. To add a new singleton: prefer `@Inject` constructor + `@Singleton`; add a `@Binds` to `AngelmanModule` only when you need to bind an interface to its implementation. Generated `Hilt_*` / `*_HiltModules*` / `*_Factory*` / `*_MembersInjector*` classes are excluded from coverage.
- **Lombok** is used heavily on VOs and some services — `@Getter`/`@Setter`/`@Builder`/`@AllArgsConstructor`. `app/src/main/lombok.config` governs behavior. If you see a missing constructor or getter, it's generated.
- **No Butterknife** — view wiring is plain `findViewById` + `setOnClickListener`. Look for `TODO(phase-6)` markers in older activities where `@OnClick` was unrolled.
- **View Binding** is enabled (`buildFeatures.viewBinding true`) and generated `*Binding.class` files are coverage-excluded. Activities mostly still use `findViewById` though — view binding is opt-in per call site.
- **Firebase config** lives in `app/google-services.json`. The `google-services` Gradle plugin requires it at build time. Firebase access goes through the BoM (`firebase-bom:33.1.2`) — do not pin individual Firebase SDK versions.
- **KakaoLink sharing** is currently stubbed. The Kakao SDK v2 dep is intentionally not declared (see `TODO(phase-8)` in `app/build.gradle`); `KaKaoTransfer` call sites no-op. Re-enable by adding `com.kakao.sdk:v2-share:<version>` from `devrepo.kakao.com` once `ShareClient` wiring lands.
- **Signing**: debug builds are signed with the committed `angeltalk.jks` (credentials are in `app/build.gradle`). Release builds are unsigned in this repo.

### Child mode lock-screen flow

This is the app's signature feature and the most fragile part of the codebase, so it gets its own callout. The flow:

1. User toggles child mode via `ApplicationManager.setChildMode()` or `changeChildMode(true)`. Both paths start `ScreenService` as a foreground service (`startForegroundService` on API 26+).
2. `ScreenService.onCreate` runtime-registers `ScreenReceiver` for `Intent.ACTION_SCREEN_OFF` with **`ContextCompat.RECEIVER_EXPORTED`**. This flag is mandatory — `RECEIVER_NOT_EXPORTED` silently drops system broadcasts on `targetSdk 33+` and the receiver will never fire. The manifest `<receiver>` entry has no `intent-filter` because `ACTION_SCREEN_OFF` cannot be delivered to manifest-declared receivers since Android 8.0.
3. `ScreenService.onStartCommand` always promotes to foreground via `startForeground(...)` regardless of intent action — needed because `START_STICKY` redelivers a null intent on restart, and skipping `startForeground` triggers `ForegroundServiceDidNotStartInTimeException` on Android 12+. On API 34+ it must pass `FOREGROUND_SERVICE_TYPE_SPECIAL_USE`, declared in the manifest with `foregroundServiceType="specialUse"` and the `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` property.
4. On `ACTION_SCREEN_OFF`, `ScreenReceiver` calls `ApplicationManager.makeChildView()`, which launches **`LockScreenActivity`** with `FLAG_ACTIVITY_NEW_TASK | CLEAR_TOP | SINGLE_TOP`. Background activity launch is allowed because the call originates from a foreground service.
5. `LockScreenActivity` opts into `setShowWhenLocked(true)` + `setTurnScreenOn(true)` (API 27+) and `requestDismissKeyguard(...)`. It hosts the `CategoryMenuLayout` and `CardViewPagerLayout` custom views inside a `FrameLayout` container, swapping between them on category click / back press. The lock-area long-press triggers `finishAndRemoveTask()`. `onBackPressed()` is intentionally a no-op — back must not dismiss the surface.

The legacy `ChildModeManager` overlay path (`TYPE_APPLICATION_OVERLAY` + `FLAG_SHOW_WHEN_LOCKED`) is **dead code for the lock screen** because Android 12+ blocks overlays from drawing above the keyguard. `ChildModeManager` is still wired in for the `TelephonyCallback` / `PhoneStateListener` path that hides/restores the surface on incoming calls — that path is **also broken** (it still calls the overlay APIs) and needs to be migrated to launch/finish `LockScreenActivity` if anyone touches it.

Other gotchas:
- `Settings.canDrawOverlays()` is still checked in `ScreenReceiver`. If the user hasn't granted "Display over other apps", the receiver returns silently. There is no in-app permission request flow — that's a known gap.
- `KeyguardManager.disableKeyguard()` in `ScreenReceiver` is deprecated and a no-op on secure lock screens. `LockScreenActivity` now calls `requestDismissKeyguard` directly, so the receiver-side call is redundant but harmless.

## Test Layout

The Hilt migration (`phase-9`) split the test sources into a "ported" and a "legacy" pile. `app/build.gradle` reflects the split via custom `sourceSets`:

- **`app/src/test-hilt/java/`** — the **active** unit test source set. Robolectric + JUnit4 + Mockito + Hilt test (`@HiltAndroidTest` + `HiltTestApplication`). All new unit tests go here. Run with `./gradlew :app:testDebugUnitTest`.
- **`app/src/test/java/`** — legacy unit tests still wired against the old hand-built `TestAngelmanApplication` + Dagger component. **Excluded from the build** until ported. ~45 files, including `DatabaseHelperTest`, the `presentation/activity/*Test` set, and the legacy `ScreenReceiverTest`. Do not run them in place — port the file to `src/test-hilt/java` using `@HiltAndroidTest` rules first, then delete the legacy copy. Some files have been ported already (look for matching paths under both directories).
- **`app/src/androidTest/java/`** — Espresso + UiAutomator instrumentation tests. **Active and compiling**. No Hilt-specific port is needed: tests run on a real device against the production `@HiltAndroidApp` `AngelmanApplication`, so the production DI graph is what they exercise. Run with `./gradlew :app:connectedDebugAndroidTest`. Per-class status and the shared helpers are documented under *Instrumentation test layout* below.

### Instrumentation test layout

Layout under `app/src/androidTest/java/angeltalk/plus/presentation/activity/`:

- **`v1/`** — the active instrumentation pile. Nine test classes, six of which are green on the current Android 15 emulator. The three failing ones (`Camera2PerformanceTest`, `MakeNewCardWithCameraTest`, `UICamera2ActivityTest`) all depend on the Camera2 capture pipeline completing inside the emulator's fake camera — the test code itself has been modernized (content matchers, DB reset, UiAutomator waits) but the capture never reaches `onCaptureCompleted` in time, so `MakeCardPreviewActivity` doesn't come up. Fixing those needs either a production-side camera mock or a physical device; it's not a test-harness issue.
- **Parent package (`activity/`)** — six top-level classes kept from the legacy E2E pile. `CreateNewCard` and `SendAndReceiveCard` have a mix of real and empty-body `@Test` methods. `DeleteCard`, `DeleteExistingCategoriesAndCreateNewCategory`, `HideShowCardsAndChangeOrderOfCards`, and `TurnKidsModeOnOffByUsingNotificationDrawer` are 11-line stubs with zero `@Test` methods — they produce `initializationError` from the JUnit4 runner on every run ("No runnable methods"). Add an `@Ignore`d placeholder method or delete the files if you need a fully green suite. `UITestSuite` references them, so update or delete that too.
- **`TestUtil.java`** — shared helpers in the parent package. See *Shared test helpers* below.

### Shared test helpers (`TestUtil`)

Prefer these over rolling your own when porting a legacy test:

- **`TestUtil.resetDatabaseToDefaults(Context)`** — truncates the `CARD` and `CATEGORY` tables and re-runs `DefaultDataGenerator`. Call this from `@Before`, and use `ActivityTestRule<>(Foo.class, true, false)` + `mActivityTestRule.launchActivity(null)` so the reset runs **before** the activity's `onCreate` reads data. Hilt-injected fields on Activities are package-private and the `v1` tests live in a different package, so this helper talks to SQLite directly instead of going through `categoryRepository` / `cardRepository`.
- **`TestUtil.grantOverlayPermission()`** — `SYSTEM_ALERT_WINDOW` is an *appop*, not a runtime permission, so `adb install -g` does **not** auto-grant it. Tests that hit `OnboardingActivity` must call this in `@Before`, otherwise `checkDrawOverlayPermission()` bounces the flow out to system Settings and the post-onboarding assertions fail.
- **`TestUtil.uiAutomatorClick(resId)`** and **`TestUtil.uiAutomatorWaitForId(resId[, timeoutMs])`** — UiAutomator-based click / wait. Use these when Espresso's click would fail with `AppNotIdleException` (e.g. `category_delete_button` in delete mode, because the `shake_anim` on category cells has `repeatCount="infinite"` and the main looper never idles) or when a click is followed by an async activity transition that Espresso's `onView` can't see (e.g. `onboarding_finish` → permission callback → `moveToCategoryMenuActivity`). UiAutomator drives clicks via the accessibility service's `ACTION_CLICK`, which doesn't care about looper idle state.
- `TestUtil.InitializeDatabase(Context, CategoryRepository, CardRepository)` — legacy repo-based variant, still used by a few tests that actually have access to the repos. Prefer `resetDatabaseToDefaults` for new code.

### Emulator / device setup

The instrumentation harness assumes:

- **Device locale is `ko_KR`** — assertions hard-code Korean strings. A `ko-KR` AVD works out of the box; on a physical device, set locale under Settings → System → Languages.
- **Animations are disabled**: Espresso requires window/transition/animator scales to be 0 on the device. This is a **sticky per-device setting**, not a gradle config. Run once after booting any fresh AVD:
  ```
  adb shell settings put global window_animation_scale 0
  adb shell settings put global transition_animation_scale 0
  adb shell settings put global animator_duration_scale 0
  ```
- **Dangerous permissions auto-grant**: `app/build.gradle` sets `installation { installOptions '-g' }`, so every install (including the per-run test install) passes `-g` and all manifest-declared dangerous permissions (`CAMERA`, `RECORD_AUDIO`, `READ_PHONE_STATE`, `READ_MEDIA_*`, `POST_NOTIFICATIONS`, …) are granted at install time. `SYSTEM_ALERT_WINDOW` is **not** covered by `-g`; tests that need it call `TestUtil.grantOverlayPermission()`.
- `SendVOCTest` makes real Firebase calls — network-dependent and may flake offline.

### Instrumentation test deps (`app/build.gradle` — `androidTestImplementation`)

- `androidx.test:runner:1.6.2`, `androidx.test:rules:1.6.1`
- `androidx.test.espresso:espresso-core:3.6.1`
- `androidx.test.ext:junit:1.2.1`
- **`androidx.test.uiautomator:uiautomator:2.3.0`** — added specifically to work around the `shake_anim` idle-wait problem and async permission transitions; see `TestUtil.uiAutomatorClick` / `uiAutomatorWaitForId`.
- `com.google.dagger:hilt-android-testing:2.52` + compiler (not currently exercised, but available for future @BindValue test modules)

### Refactoring patterns for legacy instrumentation tests

When porting a failing legacy test, the same handful of transformations usually get it green:

1. Switch `ActivityTestRule` to the `(cls, true, false)` constructor and call `launchActivity(null)` from `@Before` so `resetDatabaseToDefaults` runs before `onCreate`.
2. Replace position-based `TestUtil.childAtPosition(withId(...), N)` matchers with content-based `allOf(withId(R.id.category_title), withText("놀이"))`. The 5 default categories and their cards are deterministic under `resetDatabaseToDefaults`, so content matching is stable.
3. Delete `withText` / `withTextColor` assertions on ImageViews (`category_delete_button`, `new_category_save_button`, `category_title_cancel`, `onboarding_finish` — all ImageView/drawable-only). Use `isEnabled()` / `not(isEnabled())` for enable-state, or just `isDisplayed()`.
4. If the test hangs on `AppNotIdleException`, replace the click with `TestUtil.uiAutomatorClick(resId)`.
5. If the test fails on a `NoMatchingViewException` for a view on the next screen, replace the first post-click `onView(...)` check with `TestUtil.uiAutomatorWaitForId(resId[, longerTimeout])` — it waits across activity transitions without depending on main-looper idle.
6. Update stale card counts: the default "놀이" category has **5** cards (not 4 as some old tests expect). Run the test once after a DB reset to see the true numbers.

Coverage exclusions in `app/build.gradle`'s `coverageReport` task list the generated/boilerplate classes that are intentionally uncovered: `R`, `BuildConfig`, `Manifest`, `AngelmanApplication`, `Hilt_*`, `*_HiltModules*`, `*_Factory*`, `*_MembersInjector*`, `*_GeneratedInjector*`, `Dagger*`, `*Test*`, `*ViewBinding*`, `*Binding`, `*Builder`, `Camera2Activity*`, `VideoFragment*`, `VideoCardTextureView*`, `*Transfer*`, `DatabaseHelper`, `AbstractActivity`. If you add a new generated class family, extend that exclusion list rather than chasing coverage.

## Contribution Flow (historical)

Per `contributor.markdown`: PRs historically targeted the **`development`** branch, Travis built the APK, and it was posted to Slack. The current default branch is `master` and the project is archived, so treat these as context rather than an active workflow.
