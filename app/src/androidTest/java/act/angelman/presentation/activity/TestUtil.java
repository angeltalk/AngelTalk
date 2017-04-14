package act.angelman.presentation.activity;

import android.content.Context;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import act.angelman.data.sqlite.DatabaseHelper;
import act.angelman.data.sqlite.DefaultDataGenerator;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.domain.repository.CategoryRepository;

public class TestUtil {

    public static void InitializeDatabase(Context context, CategoryRepository categoryRepository, CardRepository cardRepository){
        List<CategoryModel> categoryModelList = categoryRepository.getCategoryAllList();
        for (CategoryModel model : categoryModelList) {
            categoryRepository.deleteCategory(model.index);
            cardRepository.deleteSingleCardsWithCategory(model.index);
        }
        new DefaultDataGenerator().insertDefaultData(DatabaseHelper.getInstance(context).getWritableDatabase());
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
}
