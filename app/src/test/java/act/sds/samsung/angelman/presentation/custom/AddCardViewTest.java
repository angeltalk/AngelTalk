package act.sds.samsung.angelman.presentation.custom;

import android.content.Intent;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.activity.CameraGallerySelectionActivity;
import act.sds.samsung.angelman.presentation.activity.CardViewPagerActivity;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AddCardViewTest extends UITest {

    private AddCardView subject;
    private CardViewPagerActivity dummyActivity;

    @Before
    public void setUp() throws Exception {
        setCategoryModel();
        dummyActivity = Robolectric.setupActivity(CardViewPagerActivity.class);
        subject = new AddCardView(dummyActivity);
    }

    @Test
    public void whenLaunched_thenShowViewsCorrectly() throws Exception {

        TextView addCardText = (TextView) subject.findViewById(R.id.add_card_text);
        assertThat(addCardText).isVisible();
        assertThat(addCardText).containsText(R.string.add_new_card);
    }

    @Test
    public void whenClickedAddCardView_thenShouldBeLaunchedCameraGallerySelectionActivity() throws Exception {
        subject.performClick();

        Intent intent = shadowOf(dummyActivity).peekNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(CameraGallerySelectionActivity.class.getCanonicalName());

    }

    private void setCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.color = ResourcesUtil.GREEN;
        ((AngelmanApplication) RuntimeEnvironment.application).setCategoryModel(categoryModel);
    }
}