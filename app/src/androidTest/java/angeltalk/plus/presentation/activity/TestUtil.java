package angeltalk.plus.presentation.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.ParcelFileDescriptor;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.internal.util.Checks;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import angeltalk.plus.data.sqlite.CardColumns;
import angeltalk.plus.data.sqlite.CategoryColumns;
import angeltalk.plus.data.sqlite.DatabaseHelper;
import angeltalk.plus.data.sqlite.DefaultDataGenerator;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.domain.repository.CategoryRepository;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TestUtil {

    public static void InitializeDatabase(Context context, CategoryRepository categoryRepository, CardRepository cardRepository){
        List<CategoryModel> categoryModelList = categoryRepository.getCategoryAllList();
        for (CategoryModel model : categoryModelList) {
            categoryRepository.deleteCategory(model.index);
            cardRepository.deleteSingleCardsWithCategory(model.index);
        }
        new DefaultDataGenerator().insertDefaultData(context, DatabaseHelper.getInstance(context).getWritableDatabase());
    }

    // Hilt-injected fields on Activities are package-private and the v1 tests live in
    // a different package, so they can't reach categoryRepository / cardRepository
    // through ActivityTestRule.getActivity(). This variant talks directly to the SQLite
    // database, which is fine for state reset (the production code uses the same
    // tables). Use this from @Before in tests that depend on default category/card data.
    public static void resetDatabaseToDefaults(Context context) {
        android.database.sqlite.SQLiteDatabase db =
                DatabaseHelper.getInstance(context).getWritableDatabase();
        db.delete(CardColumns.TABLE_NAME, null, null);
        db.delete(CategoryColumns.TABLE_NAME, null, null);
        new DefaultDataGenerator().insertDefaultData(context, db);
    }

    // SYSTEM_ALERT_WINDOW is an appop, not a runtime permission, so `adb install -g`
    // does NOT auto-grant it. Tests that hit OnboardingActivity flow through
    // checkDrawOverlayPermission() which kicks the user out to Settings if this isn't
    // granted, breaking any subsequent assertion. Call this from @Before on tests that
    // launch OnboardingActivity.
    public static void grantOverlayPermission() {
        runShell("appops set angeltalk.plus SYSTEM_ALERT_WINDOW allow");
    }

    /** Package name used for UiAutomator resource id lookups. */
    public static final String APP_PACKAGE = "angeltalk.plus";

    private static final long UIAUTOMATOR_TIMEOUT_MS = 5_000;

    /**
     * Click a view by its resource id using UiAutomator. Unlike Espresso, this does
     * not wait for the main looper to be idle, so it succeeds even while an infinite
     * animation (e.g. {@code shake_anim} on category cells in delete mode) is running.
     */
    public static void uiAutomatorClick(String resId) {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        BySelector selector = By.res(APP_PACKAGE, resId);
        UiObject2 view = device.wait(Until.findObject(selector), UIAUTOMATOR_TIMEOUT_MS);
        if (view == null) {
            throw new AssertionError("UiAutomator could not find view with id " + resId);
        }
        view.click();
    }

    /** Wait until a view with the given resource id appears in the current window. */
    public static void uiAutomatorWaitForId(String resId) {
        uiAutomatorWaitForId(resId, UIAUTOMATOR_TIMEOUT_MS);
    }

    /** Wait up to {@code timeoutMs} for a view with the given resource id to appear. */
    public static void uiAutomatorWaitForId(String resId, long timeoutMs) {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        BySelector selector = By.res(APP_PACKAGE, resId);
        UiObject2 view = device.wait(Until.findObject(selector), timeoutMs);
        if (view == null) {
            throw new AssertionError("UiAutomator could not find view with id " + resId
                    + " within " + timeoutMs + "ms");
        }
    }

    private static void runShell(String command) {
        ParcelFileDescriptor pfd = InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand(command);
        // Drain the descriptor so the shell command actually runs to completion before
        // we return — otherwise it can race with the test that immediately depends on
        // the side effect.
        try (FileInputStream in = new FileInputStream(pfd.getFileDescriptor())) {
            byte[] buf = new byte[256];
            //noinspection StatementWithEmptyBody
            while (in.read(buf) != -1) { /* drain */ }
        } catch (IOException ignored) {
        }
    }

    public static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static Matcher<View> withTextColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView warning) {
                return color == warning.getCurrentTextColor();
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }

    public static Matcher<View> withDrawable(final int drawableId) {
        return new TypeSafeMatcher<View>() {

            String resourceName;

            @Override
            protected boolean matchesSafely(View target) {
                Drawable targetDrawable;

                if (target instanceof Button){
                    targetDrawable = ((Button) target).getBackground();
                } else if(target instanceof ImageView) {
                    targetDrawable = ((ImageView) target).getDrawable();
                } else {
                    return false;
                }

                if (drawableId < 0){
                    return targetDrawable == null;
                }
                Resources resources = target.getContext().getResources();
                Drawable expectedDrawable = resources.getDrawable(drawableId);
                resourceName = resources.getResourceEntryName(drawableId);

                if (expectedDrawable == null) {
                    return false;
                }

                Bitmap bitmap = ((BitmapDrawable) targetDrawable).getBitmap();
                Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
                return bitmap.sameAs(otherBitmap);
            }


            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id: ");
                description.appendValue(drawableId);
                if (resourceName != null) {
                    description.appendText("[");
                    description.appendText(resourceName);
                    description.appendText("]");
                }
            }
        };
    }
    public static ViewInteraction checkIsDisplayed(int resId) {
        return onView(withId(resId))
                .check(matches(isDisplayed()));
    }

    public static ViewInteraction checkWithText(int resId, String text) {
        return onView(withId(resId))
                .check(matches(isDisplayed()))
                .check(matches(withText(text)));
    }

    public static void performClick(Matcher<View> viewMatcher) {
        onView(viewMatcher)
                .check(matches(isDisplayed()))
                .perform(click());
    }

    public static void performClick(int resId) {
        onView(withId(resId))
                .check(matches(isDisplayed()))
                .perform(click());
    }
}
