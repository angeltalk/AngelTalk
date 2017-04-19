package act.angelman.presentation.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.angelman.presentation.activity.CategoryMenuActivity;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateNewCard {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void byTakingPicture() throws Exception {

    }

    @Test
    public void byBringingPictureFromGallery() throws Exception {

    }

    @Test
    public void byShootingVideo() throws Exception {

    }
}