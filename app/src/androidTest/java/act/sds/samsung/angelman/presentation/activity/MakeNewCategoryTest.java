package act.sds.samsung.angelman.presentation.activity;


import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.sds.samsung.angelman.R;

import static act.sds.samsung.angelman.presentation.activity.TestUtil.childAtPosition;
import static act.sds.samsung.angelman.presentation.activity.TestUtil.withTextColor;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MakeNewCategoryTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Before
    public void setUp() throws Exception {
        TestUtil.InitializeDatabase(mActivityTestRule.getActivity().getApplicationContext(), mActivityTestRule.getActivity().categoryRepository, mActivityTestRule.getActivity().cardRepository);
    }

    @Test
    public void makeNewCategoryTest() {
        onView(newCategoryItemTitle())
                .check(matches(withText("새 카테고리")))
                .check(matches(isDisplayed()));
        onView(newCategoryItemCard())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(childAtPosition(withId(R.id.new_category_header), 1))
                .check(matches(withText("새 카테고리")))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_title))
                .check(matches(withText("카테고리 이름")))
                .check(matches(isDisplayed()));
        onView(withId(R.id.edit_category_title))
                .check(matches(isDisplayed()))
                .perform(replaceText("취소테스트"), closeSoftKeyboard())
                .perform(pressImeActionButton());

        onView(withId(R.id.category_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("취소테스트")));
        onView(withId(R.id.category_title_cancel))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.category_title))
                .check(matches(withText("카테고리 이름")))
                .check(matches(isDisplayed()));
        onView(withId(R.id.edit_category_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("")));
        onView(withId(R.id.new_category_save_button))
                .check(matches( withTextColor(getColorFromResources(R.color.white_32))));

        onView(withId(R.id.edit_category_title))
                .check(matches(isDisplayed()))
                .perform(replaceText("테스트"), closeSoftKeyboard())
                .perform(pressImeActionButton());

        onView(withId(R.id.new_category_save_button))
                .check(matches( withTextColor(getColorFromResources(R.color.white))))
                .check(matches(withText("등록")))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.category_item_title))
                .check(matches(withText("테스트")))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_item_count))
                .check(matches(withText("총 0장")))
                .check(matches(isDisplayed()));
        onView(withId(R.id.back_button))
                .perform(click());

        onView(newCategoryItemTitle())
                .check(matches(withText("테스트")));
    }

    private int getColorFromResources(int id) {
        return mActivityTestRule.getActivity().getResources().getColor(id);
    }

    @NonNull
    private Matcher<View> newCategoryItemCard() {
        return allOf(withId(R.id.category_item_card),
                childAtPosition(
                        withId(R.id.category_list),
                        5));
    }

    @NonNull
    private Matcher<View> newCategoryItemTitle() {
        return allOf(withId(R.id.category_title),
                childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                5),
                        0),0),1),1));
    }

}
