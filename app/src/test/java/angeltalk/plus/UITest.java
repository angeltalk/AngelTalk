package angeltalk.plus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import androidx.test.core.app.ApplicationProvider;

@Config(qualifiers = "ko")
public class UITest {
    protected ActivityController controller;

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

    public String getString(@StringRes int id) {
        return RuntimeEnvironment.application.getResources().getString(id);
    }

    public int getColor(@ColorRes int id) {
        return RuntimeEnvironment.application.getResources().getColor(id);
    }

    public Drawable getDrawable(@DrawableRes int id) {
        Context context = ApplicationProvider.getApplicationContext();
        return context.getResources().getDrawable(id, context.getTheme());
    }

    public float getDimension(@DimenRes int resId) {
        return RuntimeEnvironment.application.getResources().getDimension(resId);
    }
}
