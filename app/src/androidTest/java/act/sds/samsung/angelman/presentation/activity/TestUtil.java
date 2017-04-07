package act.sds.samsung.angelman.presentation.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import act.sds.samsung.angelman.data.sqlite.DatabaseHelper;
import act.sds.samsung.angelman.data.sqlite.DefaultDataGenerator;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;

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
}
