package angeltalk.plus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@Config(qualifiers = "ko")
public class UITest {
    protected ActivityController controller;

    @Before
    public void initFirebaseForTest() {
        Context ctx = ApplicationProvider.getApplicationContext();
        if (FirebaseApp.getApps(ctx).isEmpty()) {
            FirebaseApp.initializeApp(ctx,
                    new FirebaseOptions.Builder()
                            .setApiKey("test-api-key")
                            .setApplicationId("test-application-id")
                            .setProjectId("test-project")
                            .setStorageBucket("test-bucket.appspot.com")
                            .setDatabaseUrl("https://test-project.firebaseio.com")
                            .build());
        }
    }

    protected <T extends Activity> T setupActivity(Class<T> activityClass) {
        controller = Robolectric.buildActivity(activityClass);
        return activityClass.cast(controller.setup().get());
    }

    protected <T extends Activity> T setupActivityWithIntent(Class<T> activityClass, Intent intent) {
        controller = Robolectric.buildActivity(activityClass, intent);
        return activityClass.cast(controller.create().get());
    }

    protected <T extends Activity> T setupActivityWithIntentAndPostCreate(Class<T> activityClass, Intent intent) {
        controller = Robolectric.buildActivity(activityClass, intent);
        return activityClass.cast(controller.create().start().postCreate(null).newIntent(intent).resume().visible().get());
    }

    protected Context appContext() {
        return ApplicationProvider.getApplicationContext();
    }

    public String getString(@StringRes int id) {
        return appContext().getResources().getString(id);
    }

    public int getColor(@ColorRes int id) {
        return appContext().getResources().getColor(id, appContext().getTheme());
    }

    public Drawable getDrawable(@DrawableRes int id) {
        Context context = appContext();
        return context.getResources().getDrawable(id, context.getTheme());
    }

    public float getDimension(@DimenRes int resId) {
        return appContext().getResources().getDimension(resId);
    }

    /**
     * Hilt's @BindValue does not override @Inject-constructor bindings cleanly for
     * @Singleton application-scoped classes like ApplicationManager. This helper uses
     * reflection to replace an @Inject field on an already-built Activity with a test
     * mock, so verify()/when() calls work as expected.
     */
    protected <T> void replaceInjectedField(Object target, String fieldName, T value) {
        try {
            Class<?> c = target.getClass();
            while (c != null) {
                try {
                    java.lang.reflect.Field f = c.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    f.set(target, value);
                    return;
                } catch (NoSuchFieldException ignored) {
                    c = c.getSuperclass();
                }
            }
            throw new NoSuchFieldException(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
