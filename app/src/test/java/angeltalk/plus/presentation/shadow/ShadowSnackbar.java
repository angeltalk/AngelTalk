package angeltalk.plus.presentation.shadow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SnackbarContentLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadow.api.Shadow;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

@SuppressWarnings({"UnusedDeclaration", "Unchecked"})
@Implements(Snackbar.class)
public class ShadowSnackbar {
    static List<ShadowSnackbar> shadowSnackbars = Lists.newArrayList();

    @RealObject
    Snackbar snackbar;

    String text;
    private int duration;
    private int gravity;
    private int xOffset;
    private int yOffset;
    private View view;

    private static ShadowSnackbar self;

    @Implementation
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, int duration) {
        Snackbar snackbar = null;
        Constructor<Snackbar> constructor = null;
        Method hasSnackbarButtonStyleAttrMethod = null;
        try {
            constructor = Snackbar.class.getDeclaredConstructor(ViewGroup.class, View.class, android.support.design.snackbar.ContentViewCallback.class);

            if (null == constructor)
                throw new IllegalArgumentException("Seems like the constructor was not found!");

            if (Modifier.isPrivate(constructor.getModifiers())) {
                constructor.setAccessible(true);
            }

            ViewGroup parent = findSuitableParent(view);
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            hasSnackbarButtonStyleAttrMethod = Snackbar.class.getDeclaredMethod("hasSnackbarButtonStyleAttr", Context.class);
            if (Modifier.isProtected(hasSnackbarButtonStyleAttrMethod.getModifiers())) {
                hasSnackbarButtonStyleAttrMethod.setAccessible(true);
            }

            boolean isSnackbarButtonStyleAttr = (boolean) hasSnackbarButtonStyleAttrMethod.invoke(ShadowSnackbar.self, parent.getContext());
            SnackbarContentLayout content = (SnackbarContentLayout)inflater.inflate(isSnackbarButtonStyleAttr ? android.support.design.R.layout.mtrl_layout_snackbar_include : android.support.design.R.layout.design_layout_snackbar_include, parent, false);
            snackbar = constructor.newInstance(parent, content, content);
            snackbar.setText(text);
            snackbar.setDuration(duration);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            constructor.setAccessible(false);
            hasSnackbarButtonStyleAttrMethod.setAccessible(false);
        }

        shadowOf(snackbar).text = text.toString();
        shadowSnackbars.add(shadowOf(snackbar));

        return snackbar;
    }

    //this code is fetched from the decompiled Snackbar.class.
    public static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;

        do {
            if (view instanceof CoordinatorLayout) {
                return (ViewGroup) view;
            }

            if (view instanceof FrameLayout) {
//                if(view.getId() == 16908290) {
//                    return (ViewGroup)view;
//                }

                fallback = (ViewGroup) view;
            }

            if (view != null) {
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        return fallback;
    }

    // this is one of the methods which your actual Android code might invoke
    @Implementation
    public static Snackbar make(@NonNull View view, @StringRes int resId, int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }


    //just a facilitator to get the shadow
    static ShadowSnackbar shadowOf(Snackbar bar) {
        return (ShadowSnackbar) Shadow.extract(bar);
    }

    //handy for when running tests, empty the list of snackbars
    public static void reset() {
        shadowSnackbars.clear();
    }

    //some non-Android related facilitators
    public static int shownSnackbarCount() {
        return shadowSnackbars.isEmpty() ? 0 : shadowSnackbars.size();

    }

    //taken from the modus-operandus of the ShadowToast
    //a facilitator to get the text of the latest created Snackbar
    public static String getTextOfLatestSnackbar() {
        if (!shadowSnackbars.isEmpty())
            return shadowSnackbars.get(shadowSnackbars.size() - 1).text;

        return null;
    }

    //retrieve the latest snackbar that was created
    public static Snackbar getLatestSnackbar() {
        if (!shadowSnackbars.isEmpty())
            return shadowSnackbars.get(shadowSnackbars.size() - 1).snackbar;

        return null;
    }
}
