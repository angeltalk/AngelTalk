package angeltalk.plus.presentation.custom;


import android.view.View;
import android.content.Intent;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


import angeltalk.plus.R;
import angeltalk.plus.UITest;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.presentation.activity.CameraGallerySelectionActivity;
import angeltalk.plus.presentation.activity.CardViewPagerActivity;
import angeltalk.plus.presentation.manager.ApplicationManager;
import angeltalk.plus.presentation.util.ResourcesUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import org.junit.Rule;
import org.mockito.Mockito;

import angeltalk.plus.dagger.modules.AngelmanModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

@HiltAndroidTest
@UninstallModules(AngelmanModule.class)
@RunWith(RobolectricTestRunner.class)
public class AddCardViewTest extends UITest {


    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    angeltalk.plus.domain.repository.CardRepository __cardRepoMock = Mockito.mock(angeltalk.plus.domain.repository.CardRepository.class);

    @BindValue
    angeltalk.plus.domain.repository.CategoryRepository __categoryRepoMock = Mockito.mock(angeltalk.plus.domain.repository.CategoryRepository.class);
    @BindValue
    ApplicationManager applicationManager = Mockito.mock(ApplicationManager.class);

    private AddCardView subject;
    private CardViewPagerActivity dummyActivity;

    @Before
    public void setUp() throws Exception {
        hiltRule.inject();
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        dummyActivity = Robolectric.setupActivity(CardViewPagerActivity.class);
        subject = new AddCardView(dummyActivity);
    }

    @Test
    public void whenLaunched_thenShowViewsCorrectly() throws Exception {

        TextView addCardText = (TextView) subject.findViewById(R.id.add_card_text);
        assertThat(addCardText.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(addCardText.getText().toString()).isEqualTo(dummyActivity.getString(R.string.add_new_card));
    }

    @Test
    public void whenClickedAddCardView_thenShouldBeLaunchedCameraGallerySelectionActivity() throws Exception {
        subject.performClick();

        Intent intent = shadowOf(dummyActivity).peekNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(CameraGallerySelectionActivity.class.getCanonicalName());

    }

    private CategoryModel getCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.color = ResourcesUtil.GREEN;
        return categoryModel;
    }
}