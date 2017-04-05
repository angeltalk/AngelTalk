package act.sds.samsung.angelman.presentation.activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CardListActivityTest extends UITest{

    @Inject
    ApplicationManager applicationManager;

    private CardListActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        subject = setupActivity(CardListActivity.class);
    }

    @Test
    public void whenLaunched_thenShowViewCorrectly() throws Exception {
        assertThat(shadowOf(subject.titleCardListContainer.getBackground()).getCreatedFromResId()).isEqualTo(R.color.simple_background_red);
        assertThat(subject.categoryItemTitle.getText()).isEqualTo("먹을 것");
    }

    @Test
    public void whenBackbuttonClicked_thenFinish() throws Exception {
        subject.findViewById(R.id.back_button).performClick();
        assertThat(subject.isFinishing()).isTrue();
    }

    private CategoryModel getCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.index = 0;
        categoryModel.title = "먹을 것";
        categoryModel.color = ResourcesUtil.RED;
        return categoryModel;
    }
}