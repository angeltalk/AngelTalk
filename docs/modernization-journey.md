# AngelTalk Plus 현대화 여정

_아카이브된 안드로이드 앱을 2026년 Android 15 위에서 다시 살아 움직이게 한 이야기_

---

## 배경

AngelTalk Plus는 자폐 아동 등 복합 의사소통 장애(CCN)를 가진 아이들을 위한 AAC(보완대체의사소통) 앱이다. 공식적으로는 **archived** 상태 — 더 이상 적극적으로 유지보수되지 않는다. 하지만 아카이브된 프로젝트도 실제 기기에서 여전히 돌아가야 한다. 그리고 안드로이드 생태계는 6개월마다 한 번씩 "이거 더 이상 안 돼요" 메시지를 들고 찾아온다.

원래 릴리스 스택은 안드로이드 역사박물관의 전시물 같았다:

- Android Gradle Plugin 3.0.0
- `compileSdk 28` / Java 8
- `com.android.support:*` 라이브러리 (AndroidX 이전)
- Dagger 2.20 + Butterknife 8.4.0 + Firebase SDK 10.2.0
- 수제 `AngelmanComponent` + `TestAngelmanApplication`
- Travis CI + `development` 브랜치 전략

현대 툴체인이 이 조합을 빌드하려고 하면 모든 경고등이 켜진다.

이 문서는 두 개의 이야기를 묶어서 정리한다:

1. **툴체인 현대화** (phase-6 ~ phase-9) — 빌드 가능한 상태로 되돌리기
2. **이번 세션의 작업** — Child mode 잠금화면 수리 + androidTest 되살리기

---

## Part 1. 툴체인 현대화 (phase-6 ~ phase-9)

### 목표

그저 빌드가 되게 만드는 것. `./gradlew :app:assembleDebug`가 성공하고 `:app:testDebugUnitTest`가 돌아가도록 만드는 게 phase-6 ~ phase-9의 목적이었다.

### Before / After

| 항목 | 이전 | 이후 |
|---|---|---|
| Android Gradle Plugin | 3.0.0 | **8.11.1** |
| Gradle | ~4.x | **9.0.0** |
| Java source/target | 1.8 | **17** |
| `compileSdk` / `targetSdk` | 28 / — | **35 / 35** |
| `minSdk` | 23 | 23 (유지) |
| DI | Dagger 2.20 + 수제 `AngelmanComponent` | **Hilt 2.52** + `@HiltAndroidApp` |
| View binding | Butterknife 8.4.0 (`@BindView`/`@OnClick`) | `findViewById` + `setOnClickListener` (`viewBinding` 옵트인) |
| Firebase | 10.2.0 (개별 SDK) | **BoM 33.1.2** |
| Glide | (old) | 4.16.0 |
| 지원 라이브러리 | `com.android.support:*` | **AndroidX 전역** |

### 연쇄 반응

한 가지 업그레이드가 다음을 트리거한다:

- **AGP 3 → 8** → Gradle 호환성이 깨진다 → **Gradle 9.0.0**
- **Gradle 9** → Java 17 필요 → 소스/타겟 1.8 → **17**
- **Java 17 + targetSdk 35** → Butterknife 8.4.0 annotation processor가 터진다 → **Butterknife 전면 제거** → 수백 개의 `@BindView` / `@OnClick`을 `findViewById` + `setOnClickListener`로 푸는 대수술. 지금도 `TODO(phase-6)` 마커가 코드베이스에 남아있다.
- **Dagger 2.20** → Java 17 annotation processing API와 충돌 → **Hilt 2.52로 전환** → 수제 `AngelmanComponent` 삭제 → `AngelmanModule`은 `@Module @InstallIn(SingletonComponent.class)`로 재작성.
- **Firebase 10.2.0** → google-services 플러그인의 신버전과 호환 안 됨 → **Firebase BoM 33.1.2**로 통합.
- **`com.android.support:*`** → AndroidX 전역 이전.

### 여전히 남은 부채

Phase 6 ~ 9는 "빌드가 통과한다"를 목표로 했기 때문에 일부는 스텁으로 남겨졌다:

- **ACRA 크래시 리포팅** — `@ReportsCrashes` annotation API가 5.x에서 바뀜. 매니페스트 `kakao_app_key` 메타데이터는 남아있지만 런타임 크래시 핸들러는 비활성. `TODO(phase-9)`.
- **Kakao SDK v2 공유** — `com.kakao.sdk:v2-share` 의존성이 빠져있고 `KaKaoTransfer` 호출 지점은 no-op. `TODO(phase-8)`.
- **legacy 단위 테스트 45개** — `src/test/java` 아래, 수제 `TestAngelmanApplication` + Dagger 컴포넌트에 묶여있어서 빌드에서 제외됨. 포팅된 것들은 `src/test-hilt/java`로 이전.
- **instrumentation 테스트** — `src/androidTest/java`. 빌드에서 제외되어 있었음 (`java.srcDirs = []`). **이게 이번 세션 작업의 시작점이 된다.**

---

## Part 2. Child Mode 잠금화면 재생

### 증상

> "angeltalk 은 child mode 가 되면 android lock screen 에 angeltalk 화면이 나와야 하는데 현재 나오고 있지 않아"

이 앱의 시그니처 기능이다. 아이가 전원 버튼을 눌러 화면이 꺼졌다가 다시 켜졌을 때, 일반 잠금화면 대신 AngelTalk의 카드 메뉴가 나와야 한다. 키오스크 모드 같은 동작. 그런데 동작하지 않았다.

### 진단 — 버그는 하나가 아니었다

코드는 여러 군데에서 동시에 망가져 있었다.

#### 결정적 버그 ❶ — `ScreenService.java:42`

```java
ContextCompat.registerReceiver(this, mReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
```

`Intent.ACTION_SCREEN_OFF`는 시스템 프로세스(다른 UID)에서 보내는 protected broadcast다. `targetSdk 33+`에서는 런타임 등록 리시버가 시스템 브로드캐스트를 받으려면 **`RECEIVER_EXPORTED`로 등록해야 한다**. `RECEIVER_NOT_EXPORTED`는 시스템 브로드캐스트를 조용히 드롭한다.

즉, **`ScreenReceiver.onReceive()`가 아예 호출된 적이 없었다**.

추가로 `AndroidManifest.xml`의 `<receiver>` 블록에 있는 `ACTION_SCREEN_OFF` intent-filter는 **죽은 코드**다 — Android 8.0부터 `ACTION_SCREEN_OFF`는 매니페스트 등록 리시버로 전달되지 않는다. 무조건 런타임 등록이어야 한다.

#### 결정적 버그 ❷ — `ScreenService.java:48`

```java
if (intent != null && intent.getAction() == null) { ... startForeground ... }
```

`START_STICKY`로 재시작될 때 시스템이 **null intent**를 전달한다. 위 조건은 `intent != null`에서 false가 되어 **`startForeground`가 호출되지 않는다**. Android 12+에서는 포그라운드 서비스 시작 시간 제한(5초)을 넘겨서 `ForegroundServiceDidNotStartInTimeException`로 서비스가 죽는다.

수정: 조건 제거. `onStartCommand`에서 항상 `startForeground` 호출. API 34+는 `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` 명시.

#### 결정적 버그 ❸ — `ApplicationManager.setChildMode()`

```java
public void setChildMode() {
    ...
    context.startService(screenService);  // ← Android 8+ 백그라운드에서 호출 시 IllegalStateException
}
```

`changeChildMode()` 경로는 `startForegroundService()`로 올바르게 처리돼 있었는데 `setChildMode()` 쪽은 안 돼있었다. 두 경로가 일관되지 않았다. `setChildMode()`도 `Build.VERSION.SDK_INT >= O`일 때 `startForegroundService()` 사용하도록 통일.

### 더 깊은 문제 — 오버레이 자체가 동작 안 함

위 세 가지를 고쳤더니 리시버는 살아나고 서비스도 안정되었다. 사용자 테스트: **"에뮬레이터에서는 동작하지 않네요"**.

원인: `ChildModeManager`는 `TYPE_APPLICATION_OVERLAY` + `FLAG_SHOW_WHEN_LOCKED` + `FLAG_DISMISS_KEYGUARD` 조합으로 `WindowManager`에 윈도우를 추가하는 방식이었다. **Android 12+는 이 조합이 잠금화면 위에 그려지는 것을 정책적으로 막는다**. `TYPE_SYSTEM_ERROR`는 API 29에서 완전히 무력화됐고, `TYPE_APPLICATION_OVERLAY`도 키가드 위에 뜨려면 별도 exemption이 필요하다. `KeyguardManager.disableKeyguard()`는 API 13부터 deprecated, 보안 잠금화면에서는 no-op.

결론: **오버레이 전략 자체를 버려야 한다**.

### 해결 — Activity 기반 재설계

**`LockScreenActivity`**라는 전용 액티비티를 만들었다:

```java
@AndroidEntryPoint
public class LockScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);   // Android 8.1+ 공식 지원
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) km.requestDismissKeyguard(this, null);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        container = new FrameLayout(this);
        setContentView(container);
        showCategoryMenu();  // 기존 CategoryMenuLayout/CardViewPagerLayout 호스팅
    }

    @Override
    public void onBackPressed() {
        // 키오스크 모드 — 의도적 no-op
    }
}
```

핵심은:

- `setShowWhenLocked(true)` + `setTurnScreenOn(true)` — Android 8.1(API 27)부터 공식 지원되는 "잠금화면 위 액티비티" 방식
- 기존 `CategoryMenuLayout` / `CardViewPagerLayout` 커스텀 뷰를 `FrameLayout` 컨테이너에 호스트 → **UI 자산 100% 재사용**, 재설계 비용 제로
- 매니페스트에서 `android:showWhenLocked="true"` + `android:turnScreenOn="true"` + `android:launchMode="singleInstance"`
- `ApplicationManager.makeChildView()`를 오버레이 호출 대신 `startActivity(LockScreenActivity)` + `FLAG_ACTIVITY_NEW_TASK`로 변경. 포그라운드 서비스(`ScreenService`)에서 호출되므로 백그라운드 액티비티 시작 제한은 면제됨

### 결과

사용자 확인: **"정상 동작합니다"**. 잠금화면 → 전원 버튼 → AngelTalk이 덮는 동작이 Android 15 에뮬레이터에서 복원됐다.

### 알려진 한계 (정리 대상)

- **`ChildModeManager.processByPhoneStatus`** — 전화 수신 시 `createAndAddCategoryMenu()` 오버레이 경로를 여전히 호출. 이 경로는 이제 죽었으므로 통화 종료 후 잠금화면 복귀가 안 됨. `LockScreenActivity` 재시작으로 재배선 필요.
- **`ScreenReceiver.disableKeyguard()`** — `LockScreenActivity`가 직접 `requestDismissKeyguard`를 호출하므로 redundant. 제거 가능.
- **`SYSTEM_ALERT_WINDOW` 권한 요청 흐름** — `ScreenReceiver:31`에서 `Settings.canDrawOverlays()` 체크 후 조용히 return. 사용자에게 권한 설정 화면으로 유도하는 UX가 없음.

---

## Part 3. androidTest 되살리기

### 시작점

```gradle
sourceSets {
    androidTest {
        java.srcDirs = []   // 👈 비활성화되어 있었다
    }
}
```

CLAUDE.md에는 "phase-9에서 Hilt test rules로 포팅 대기 중"이라고 적혀있었다. 16개의 파일이 비활성화되어 있었다.

사용자 요청: **"androidTest 도 정상수행 할 수 있게 수정해줘"**.

### 깜짝 반전 — Hilt 포팅은 필요 없었다

큰 작업일 거라 예상하고 접근했다. 그런데 실제로 살펴보니 **androidTest는 Hilt 포팅이 필요 없었다**. 이유:

- 단위 테스트(`src/test/java`)는 Robolectric + 수제 `TestAngelmanApplication` + 수제 Dagger 컴포넌트 조합이었다. 그래서 Hilt로 포팅 필요.
- 인스트루멘테이션 테스트(`src/androidTest/java`)는 **실제 디바이스에서 실제 앱 프로세스로 돌아간다**. `@HiltAndroidApp`이 붙은 production `AngelmanApplication`이 그대로 부트스트랩된다. 코드에 `TestAngelmanApplication` 참조도 없었다.

진짜 막고 있는 건 **두 가지**였다:

1. `android.test.suitebuilder.annotation.LargeTest` — Android 9(API 28)에서 제거된 패키지. 15개 파일에 남아있었다.
2. `java.srcDirs = []` 블록 자체.

수정:

```bash
# (1) 15개 파일에 sed 일괄 치환
find app/src/androidTest -name '*.java' -print0 \
  | xargs -0 sed -i '' 's|android\.test\.suitebuilder\.annotation\.LargeTest|androidx.test.filters.LargeTest|g'
```

```gradle
// (2) build.gradle에서 androidTest 블록 삭제 (기본 위치로 복원)
sourceSets {
    main { res.srcDirs = [...] }
    test { java.srcDirs = ['src/test-hilt/java'] }
    // androidTest 기본 위치 (src/androidTest/java) 사용
}
```

이것만으로 **첫 시도에 컴파일 통과**. 테스트 APK까지 빌드 성공.

### 첫 실행의 충격

에뮬레이터(Android 15 / arm64-v8a / ko-KR)에 설치하고 전체 suite 실행:

```
Starting 37 tests on Resizable_Experimental(AVD) - 15
...
Finished 20 tests
13 failure(s)
```

37개가 discovered, 20개가 완료, 13개가 실패. 게다가 37과 20의 불일치 — 17개가 어디로 갔나?

### 실패 분류

실패들을 카테고리로 나누니 패턴이 드러났다:

| 카테고리 | 개수 | 원인 |
|---|---|---|
| `initializationError` | 4 | **빈 placeholder 클래스** — `@Test` 메서드가 0개인 11줄짜리 stub |
| `NoMatchingViewException` | 4 | 기대한 view id가 없음 (UI 변경 / DB 상태) |
| `AssertionFailedWithCauseError` | 3 | 텍스트 불일치 (`"새 카테고리"`, `"총 4장"`, `"삭제"`) |
| `AmbiguousViewMatcherException` | 1 | 같은 id가 3개 매칭 |
| `PerformException` / `AppNotIdleException` | 1 | 무한 애니메이션으로 looper idle 대기 실패 |
| 미실행 | ~17 | discovery는 됐지만 class init 실패한 클래스의 메서드들 |

**실패 중 단 하나도 "인프라 문제"가 아니었다.** 전부 테스트 코드 자체의 사전 부채(pre-existing debt)였다. 즉, `java.srcDirs` 복원 + `sed` 한 번으로 인프라 복원은 이미 완료된 셈이었다.

### 흥미로운 사전 부채 발견

#### "삭제" 텍스트 매칭이 왜 실패했나?

`category_delete_button`은 **ImageView**다:

```xml
<ImageView
    android:id="@+id/category_delete_button"
    android:src="@drawable/btn_delete_dark"/>
```

텍스트가 없다. `withText("삭제")` 어설션은 layout이 TextView → ImageView로 변경된 순간부터 통과할 수 없었다. **영원히 실패할 어설션이 테스트 코드에 남아있었다**.

`new_category_save_button`도 똑같이 ImageView였다. `setTextColor`가 아니라 `setImageAlpha`로 enable/disable을 표현한다. 테스트의 `TestUtil.withTextColor(...)` 어설션은 view가 TextView가 아니라서 즉시 실패.

#### "총 4장"이 왜 "총 5장"이어야 하나?

`DefaultDataGenerator`가 "놀이" 카테고리에 **5개 카드**를 삽입한다(색칠해요/그네/블럭/크레파스/색종이). 테스트는 4장을 기대하고 있었다. 어느 시점에 한 카드가 추가됐는데 테스트는 업데이트되지 않았다.

#### 위치 기반 매처의 공포

많은 테스트가 이런 걸 쓰고 있었다:

```java
TestUtil.childAtPosition(TestUtil.childAtPosition(TestUtil.childAtPosition(TestUtil.childAtPosition(
    TestUtil.childAtPosition(
        IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
        5),
    0),0),1),1)
```

이건 GridView의 5번째 child의 0번째 child의 0번째 child의 1번째 child의 1번째 child를 찾는다. **View 계층이 한 단계 깊어지기만 해도 모든 테스트가 깨진다.** Android Studio의 "Record Espresso Test" 기능이 생성하는 전형적 패턴이지만 maintain 불가능하다.

---

## Part 4. 점진적 테스트 복원 & UiAutomator 도입

사용자와 "한 클래스씩 점진적으로" 고치는 방향으로 결정.

### 공통 패턴 추출

세 개 테스트를 연속으로 통과시킨 후 공통 리팩터링 패턴이 나타났다:

1. **Lazy activity launch**: `ActivityTestRule(cls, true, false)` + `@Before` 안에서 `launchActivity(null)`
2. **DB 리셋**: `TestUtil.resetDatabaseToDefaults()`를 activity create 전에 호출
3. **컨텐츠 기반 매처**: `allOf(withId(R.id.category_title), withText("놀이"), isDisplayed())`
4. **ImageView 어설션 교체**: `withText`/`withTextColor` → `isEnabled` / `not(isEnabled)` / `isDisplayed`
5. **리소스 참조**: 하드코딩된 한글 문자열 → `R.string.new_category_name` 등

이 패턴 하나로:

- ✅ **MakeNewCategoryTest**
- ✅ **CardViewPagerTest**
- ✅ **MoveToAnotherCategoryTest**

세 개가 깨끗하게 통과했다.

### 난제 ❶ — `DeleteCategoryTest`의 `AppNotIdleException`

Espresso는 click 전에 main looper가 idle 상태가 될 때까지 기다린다. `category_delete_button`을 누르면 delete mode로 전환되면서 production code의 `shake_anim`이 카테고리 셀에 적용된다:

```xml
<rotate
    android:duration="70"
    android:repeatCount="infinite"
    android:repeatMode="reverse" .../>
```

**`repeatCount="infinite"`**. 이게 60Hz로 Choreographer 프레임 콜백을 계속 쌓는다. Main looper는 영원히 idle이 되지 않는다. Espresso는 60초 대기 후 timeout — `Looped for 7206 iterations`.

원래 테스트 파일에는 이미 이 문제에 대한 TODO 주석이 달려있었다:

```java
// TODO: 삭제버튼 -> 오들오들에니메이션 코드 진행시 perform(click())과 충돌...
//       espresso에서 looping animation 지원안함
```

몇 년 전부터 알려진 문제였지만 해결이 안 된 채로 남아있었다.

### 난제 ❷ — `OnboardingAndCategoryMenuViewTest`의 보이지 않는 전환

`onboarding_finish` 클릭 → `requestPermissions` → `onRequestPermissionsResult` → `checkDrawOverlayPermission` → `moveToCategoryMenuActivity`. 이 체인이 전부 비동기. Espresso click 후 다음 `onView(withId(R.id.drawer_meun))`가 CategoryMenuActivity 도착 전에 실행돼서 실패. 15초 sleep을 넣어도 실패.

logcat을 캡처해 보니:

```
I/ActivityTaskManager: Displayed angeltalk.plus/.presentation.activity.OnboardingActivity for user 0: +506ms
```

하지만 `CategoryMenuActivity START` 로그는 없었다. 즉, **Espresso click 후 navigation이 아예 일어나지 않았다**. 권한 플로우의 어느 지점에서 silently stuck된 상태.

### UiAutomator 도입

이 두 가지 문제를 해결하기 위해 `androidx.test.uiautomator:uiautomator:2.3.0`을 의존성에 추가했다. UiAutomator는 Espresso와 근본적으로 다르다:

| | Espresso | UiAutomator |
|---|---|---|
| 실행 위치 | 대상 앱 프로세스 내부 | Instrumentation 프로세스 |
| UI 접근 방식 | view 계층 직접 탐색 | Accessibility 서비스 |
| Click 구현 | `MotionEvent` 합성 주입 | `ACTION_CLICK` accessibility action |
| Idle 대기 | Main looper가 idle이 될 때까지 기다림 | 대기하지 않음 |
| 다중 앱 | 한 앱 내에서만 | 디바이스 전체 |

무한 애니메이션이 돌아도, 여러 앱 사이를 넘나들어도, 시스템 UI와 상호작용해도 UiAutomator는 개의치 않는다.

`TestUtil`에 두 개의 헬퍼를 추가:

```java
public static void uiAutomatorClick(String resId) {
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    BySelector selector = By.res(APP_PACKAGE, resId);
    UiObject2 view = device.wait(Until.findObject(selector), UIAUTOMATOR_TIMEOUT_MS);
    if (view == null) {
        throw new AssertionError("UiAutomator could not find view with id " + resId);
    }
    view.click();
}

public static void uiAutomatorWaitForId(String resId, long timeoutMs) { ... }
```

### 적용 결과

**DeleteCategoryTest**: Espresso click 대신 `TestUtil.uiAutomatorClick("category_delete_button")` + 루프. 200줄짜리 fragile E2E 테스트가 ~25줄로 단순화되면서 **PASS**.

```java
@Test
public void deleteCategoryTest() {
    TestUtil.uiAutomatorClick("category_delete_button");

    // 4개 삭제 → 각각 confirm
    for (int i = 0; i < 4; i++) {
        TestUtil.uiAutomatorClick("category_item_card");
        TestUtil.uiAutomatorClick("confirm_button");
    }

    // 5번째 클릭 — "최소 1개" alert
    TestUtil.uiAutomatorClick("category_item_card");
    TestUtil.uiAutomatorWaitForId("alert_message");
    TestUtil.uiAutomatorClick("confirm_button");

    // MakeCategoryActivity로 자동 이동
    TestUtil.uiAutomatorWaitForId("new_category_header");
}
```

**OnboardingAndCategoryMenuViewTest**: `onboarding_finish` click을 Espresso에서 UiAutomator로 교체 + `uiAutomatorWaitForId("drawer_meun", 15_000)`. **PASS**.

흥미로운 관찰: 완전히 동일한 "버튼 클릭"이지만, Espresso의 `MotionEvent` 합성 경로는 이 production permission flow에서 silently stuck됐는데, UiAutomator의 `ACTION_CLICK` 경로는 문제없이 통과했다. 실제 터치 이벤트 dispatching과 accessibility의 click action 경로가 다른 코드 경로를 탄다는 뜻이다. **진짜로 둘은 대체재가 아니라 보완재다**.

### 최종 상태 (v1 패키지)

| 상태 | 테스트 | 적용 패치 |
|---|---|---|
| ✅ | MakeNewCategoryTest | content matcher + ImageView 어설션 교체 |
| ✅ | CardViewPagerTest | DB 리셋 + 카드 수 4→5 |
| ✅ | MoveToAnotherCategoryTest | content matcher + DB 리셋 |
| ✅ | DeleteCategoryTest | UiAutomator (shake_anim 우회) |
| ✅ | OnboardingAndCategoryMenuViewTest | UiAutomator (permission flow race 우회) |
| ✅ | SendVOCTest | (손대지 않음, 원래 통과) |
| ❌ | Camera2PerformanceTest | 에뮬레이터 Camera2 fake pipeline |
| ❌ | MakeNewCardWithCameraTest | 같은 원인 |
| ❌ | UICamera2ActivityTest | 같은 원인 |

**9개 중 6개 통과**. 남은 3개는 모두 카메라 캡처 파이프라인에 의존. 에뮬레이터의 가상 카메라가 Camera2 `onCaptureCompleted` 콜백을 안정적으로 발화시키지 못하는 문제다. 테스트 코드 자체는 수정 완료 — production-side 카메라 mock이나 실기기가 있으면 풀릴 가능성이 높다.

---

## 얻은 인프라 (재사용 가능)

### Test 쪽 헬퍼 (`TestUtil`)

- **`resetDatabaseToDefaults(Context)`** — Hilt 필드 패키지 가시성 문제를 우회해서 SQLite에 직접 default 카테고리 5개 + 카드 20개 재생성
- **`grantOverlayPermission()`** — `SYSTEM_ALERT_WINDOW` appop 부여 (adb install `-g`로는 안 되는 이유: runtime permission이 아닌 appop)
- **`uiAutomatorClick(resId)`** / **`uiAutomatorWaitForId(resId, timeoutMs)`**
- 기존 `InitializeDatabase` (repo 버전) 유지

### Build 설정

- **`installation { installOptions '-g' }`** — 모든 install에 dangerous permission 자동 부여
- **`androidx.test.uiautomator:uiautomator:2.3.0`** 의존성 추가

### 디바이스 setup 체크리스트

- `ko_KR` locale (어설션이 한글 하드코딩)
- 애니메이션 sticky global settings 비활성화:
  ```
  adb shell settings put global window_animation_scale 0
  adb shell settings put global transition_animation_scale 0
  adb shell settings put global animator_duration_scale 0
  ```
- SAW appop은 자동으로 test가 grant (위 헬퍼 경유)

### Production 쪽

- **`LockScreenActivity`** — 현대 Android에서 잠금화면 위에 뜨는 표준 방식
- **`ScreenService` foreground 승격 로직** — null intent 재시작 케이스 대응
- **`RECEIVER_EXPORTED` flag** — 시스템 브로드캐스트 수신
- **`setChildMode()` / `changeChildMode()` 통일** — `startForegroundService()` 일관 사용

---

## 교훈

### 1. 오래된 테스트 코드의 실패 원인 80%는 매처다

부서진 instrumentation 테스트 9개를 분석해 보면 실패 원인은 거의 전부 **너무 구체적인 위치 기반 매처** 또는 **stale 리소스 매칭**이었다. 진짜 production 로직 변화로 인한 실패는 한 건도 없었다.

테스트를 내구성 있게 만드는 첫 번째 규칙: **위치에 의존하지 말고 의미(content)에 의존하라**. Android Studio의 "Record Espresso Test" 기능이 생성하는 `childAtPosition(...)` 체인은 편해 보이지만 시한폭탄이다.

### 2. Espresso와 UiAutomator는 상호 보완적이다

Espresso 한 개로 모든 걸 해결할 수 있다는 기대는 현실적이지 않다. 무한 애니메이션이 있는 화면, 권한 flow가 얽힌 activity 전환, 시스템 UI와의 상호작용 — 이런 건 UiAutomator가 낫다. 하지만 view matcher의 풍부함(`hasSibling`, `isDescendantOfA`, custom matcher 등)은 Espresso가 낫다.

**한 테스트 안에서 둘을 섞어 쓰는 것**이 답이다. UiAutomator로 click, Espresso로 assertion 같은 식으로.

### 3. "archived 프로젝트"는 절대로 정적이지 않다

Android 생태계의 하위 호환성 파괴는 여전히 현재진행형이다:

- `RECEIVER_NOT_EXPORTED`의 동작은 `targetSdk 33`에서 추가됐다
- `TYPE_APPLICATION_OVERLAY` + `FLAG_SHOW_WHEN_LOCKED` 제한은 Android 12에서 들어왔다
- `android.test.suitebuilder`는 Android 9에서 제거됐다
- `START_STICKY` 재시작 시의 foreground 승격 5초 제한은 Android 12에서 강제됐다

아카이브된 프로젝트라도 최신 기기에서 돌아가게 하려면 2년마다 한 번씩은 누군가 와서 이것들을 고쳐야 한다. "archived = 건드릴 일 없음"이 아니다.

### 4. 문서와 코드의 drift는 실재한다

이 세션 시작 시 `CLAUDE.md`는 "`compileSdk 28`, Dagger 2.20, Butterknife 8.4.0"을 말하고 있었다. 실제 코드는 "`compileSdk 35`, Hilt 2.52, Butterknife 제거"였다. 누군가 phase-6 ~ phase-9를 진행했지만 문서는 업데이트되지 않았다.

이번 세션에서 `CLAUDE.md`를 두 번 대폭 갱신했다. **문서 업데이트가 코드 리뷰의 체크리스트 항목이 아니라면 drift는 불가피하다**. 차라리 `CLAUDE.md`에 "이 내용은 X일 기준" 같은 timestamp를 박아서 신뢰도를 표현하는 게 나을 수도 있다.

### 5. 작은 인프라 투자는 큰 수익을 낸다

`TestUtil.resetDatabaseToDefaults`와 `uiAutomatorClick`는 각각 30줄도 안 되는 헬퍼지만, 이 둘 덕분에 5개 테스트가 점진적으로 통과했다. 그리고 새 테스트를 쓸 때도 이 패턴을 따라가면 된다.

**매번 다른 방식으로 같은 문제를 푸는 것보다, 헬퍼 한 번 만드는 게 낫다**. 이 격언은 너무 많이 들어서 식상하지만, 실제로 그걸 실천하는 순간이 오면 효과가 바로 체감된다.

### 6. 진단보다 실행이 비싸다

초기 진단에서 나는 카메라 관련 테스트 3개가 "dead 패키지 참조 수정 + 타이밍 조정만 하면 될 것" 같다고 가볍게 추정했다. 실제로는 에뮬레이터 Camera2 fake pipeline 자체가 `onCaptureCompleted`를 안정적으로 발화시키지 않는 깊은 문제였다.

반면 `MakeNewCategoryTest`는 "색상 매처 이슈로 거의 다 된 테스트"일 거라 봤는데, 실제로는 ImageView에 대한 잘못된 어설션 + 하드코딩 문자열 + 위치 매처 3중 콤보였다. 진단은 코드를 실제로 실행해 보기 전까지는 50% 맞힐 수 있으면 잘 하는 거다.

---

## 다음 작업

아직 손 대지 못한 것들:

1. **카메라 테스트 3개** — 에뮬레이터 Camera2 fake pipeline이나 production-side 카메라 mock이 필요. 실기기에서는 통과할 가능성 있음. CI에서는 어렵다.
2. **parent package의 4개 빈 stub 클래스** — `DeleteCard`, `DeleteExistingCategoriesAndCreateNewCategory`, `HideShowCardsAndChangeOrderOfCards`, `TurnKidsModeOnOffByUsingNotificationDrawer`. 각각 11줄이고 `@Test` 메서드가 0개. `@Ignore`한 dummy 메서드 추가 또는 파일 삭제. `UITestSuite.java`도 같이 정리 필요.
3. **`ChildModeManager`의 phone state 핸들러** — 잠금화면 재설계 후에도 전화 수신 시 오버레이 재생성 경로는 그대로 남아있어 여전히 깨져 있음. `LockScreenActivity` 시작/종료로 재배선 필요.
4. **`ScreenReceiver`의 deprecated `KeyguardManager.disableKeyguard()` 호출** — `LockScreenActivity`가 `requestDismissKeyguard`를 직접 호출하므로 redundant. 정리 가능.
5. **legacy 단위 테스트 45개** — `src/test/java` → `src/test-hilt/java`로 Hilt 테스트 rule 사용해서 포팅.
6. **`SYSTEM_ALERT_WINDOW` 권한 요청 UX** — 첫 진입 시 사용자에게 권한 설정 화면으로 유도하는 흐름 부재.
7. **phase-8 Kakao SDK v2 공유 복구** — `KaKaoTransfer`가 현재 no-op.
8. **phase-9 ACRA 5 크래시 리포팅 복구** — annotation-based config 재배선.

---

## 마무리

아카이브된 프로젝트를 돌보는 건 정원 가꾸기에 가깝다. 매년 같은 잡초가 약간 다른 모양으로 돌아온다. 이번 라운드에서 배운 패턴은 다음 라운드에 그대로 써먹을 수 있다.

이 문서가 다음번 누군가가 AngelTalk Plus의 코드를 살펴볼 때, "아 그때 이런 결정들을 했구나"를 아는 데 도움이 되길 바란다. 그리고 무엇보다 — `ActivityTestRule(..., true, false)`, 컨텐츠 기반 매처, 그리고 shake_anim 앞에서는 UiAutomator를 기억하길.

---

## 참고 자료

### 이번 세션 주요 수정 파일

**Production**
- `app/src/main/java/angeltalk/plus/presentation/service/ScreenService.java` — RECEIVER_EXPORTED + foreground 승격 수정
- `app/src/main/java/angeltalk/plus/presentation/manager/ApplicationManager.java` — `setChildMode`/`changeChildMode` 통일, `makeChildView`를 `LockScreenActivity` 기반으로 교체
- `app/src/main/java/angeltalk/plus/presentation/activity/LockScreenActivity.java` — **신규**
- `app/src/main/AndroidManifest.xml` — `LockScreenActivity` 등록, `ScreenReceiver` 죽은 intent-filter 제거

**Test 인프라**
- `app/build.gradle` — androidTest source set 복원, `installation { installOptions '-g' }`, UiAutomator 의존성
- `app/src/androidTest/java/angeltalk/plus/presentation/activity/TestUtil.java` — `resetDatabaseToDefaults`, `grantOverlayPermission`, `uiAutomatorClick`, `uiAutomatorWaitForId` 추가

**Test 자체**
- `v1/MakeNewCategoryTest.java` — content matcher + ImageView 어설션
- `v1/CardViewPagerTest.java` — DB 리셋 + 카드 수 수정
- `v1/MoveToAnotherCategoryTest.java` — content matcher + DB 리셋
- `v1/DeleteCategoryTest.java` — 200줄 → 25줄, UiAutomator
- `v1/OnboardingAndCategoryMenuViewTest.java` — UiAutomator click + 권한 grant
- `v1/Camera2PerformanceTest.java` — dead 패키지 참조 제거, UiAutomator 대기 (여전히 실패)
- 15개 파일 — `android.test.suitebuilder.annotation.LargeTest` → `androidx.test.filters.LargeTest` 일괄 교체

### 실행 명령

```bash
# 단일 테스트 실행
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=angeltalk.plus.presentation.activity.v1.MakeNewCategoryTest

# 패키지 전체
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=angeltalk.plus.presentation.activity.v1

# 디바이스 prep (에뮬레이터 기준, 한 번만)
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

### 관련 문서

- `CLAUDE.md` — 프로젝트 전반 가이드. Test Layout 섹션에 이 작업의 결과물이 반영되어 있음.
- `contributor.markdown` — 과거 contribution flow (Travis + `development` 브랜치). 지금은 historical context.
