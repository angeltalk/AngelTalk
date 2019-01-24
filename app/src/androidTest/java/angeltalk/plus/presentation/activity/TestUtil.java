package angeltalk.plus.presentation.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import angeltalk.plus.data.sqlite.DatabaseHelper;
import angeltalk.plus.data.sqlite.DefaultDataGenerator;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.domain.repository.CategoryRepository;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class TestUtil {

    public static void InitializeDatabase(Context context, CategoryRepository categoryRepository, CardRepository cardRepository){
        List<CategoryModel> categoryModelList = categoryRepository.getCategoryAllList();
        for (CategoryModel model : categoryModelList) {
            categoryRepository.deleteCategory(model.index);
            cardRepository.deleteSingleCardsWithCategory(model.index);
        }
        new DefaultDataGenerator().insertDefaultData(context, DatabaseHelper.getInstance(context).getWritableDatabase());
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
