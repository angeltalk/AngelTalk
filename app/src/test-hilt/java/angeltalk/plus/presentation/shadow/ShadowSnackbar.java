package angeltalk.plus.presentation.shadow;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.Lists;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

import java.util.List;

@SuppressWarnings({"UnusedDeclaration", "Unchecked"})
@Implements(Snackbar.class)
public class ShadowSnackbar {
    static final List<ShadowSnackbar> shadowSnackbars = Lists.newArrayList();

    @RealObject
    Snackbar snackbar;

    String text;

    @Implementation
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, int duration) {
        Snackbar bar = Shadow.directlyOn(
                Snackbar.class,
                "make",
                ClassParameter.from(View.class, view),
                ClassParameter.from(CharSequence.class, text),
                ClassParameter.from(int.class, duration));
        ShadowSnackbar shadow = shadowOf(bar);
        shadow.text = text.toString();
        shadowSnackbars.add(shadow);
        return bar;
    }

    @Implementation
    public static Snackbar make(@NonNull View view, @StringRes int resId, int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    static ShadowSnackbar shadowOf(Snackbar bar) {
        return (ShadowSnackbar) Shadow.extract(bar);
    }

    public static void reset() {
        shadowSnackbars.clear();
    }

    public static int shownSnackbarCount() {
        return shadowSnackbars.size();
    }

    public static String getTextOfLatestSnackbar() {
        if (!shadowSnackbars.isEmpty()) {
            return shadowSnackbars.get(shadowSnackbars.size() - 1).text;
        }
        return null;
    }

    public static Snackbar getLatestSnackbar() {
        if (!shadowSnackbars.isEmpty()) {
            return shadowSnackbars.get(shadowSnackbars.size() - 1).snackbar;
        }
        return null;
    }
}
