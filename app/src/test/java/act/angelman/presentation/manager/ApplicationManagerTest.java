package act.angelman.presentation.manager;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.angelman.BuildConfig;
import act.angelman.domain.model.CategoryModel;
import act.angelman.presentation.shadow.ShadowKakaoLink;
import act.angelman.presentation.util.ResourcesUtil;

import static act.angelman.presentation.util.ResourceMapper.IconType.SCHOOL;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = ShadowKakaoLink.class)
public class ApplicationManagerTest {

    private ApplicationManager subject;

    @Before
    public void setUp() throws Exception {
        subject = new ApplicationManager(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void isFirstLaunchedTest() throws Exception {
        assertThat(subject.isFirstLaunched()).isTrue();
    }

    @Test
    public void setNotFirstLaunchedTest() throws Exception {
        subject.setNotFirstLaunched();
        assertThat(subject.isFirstLaunched()).isFalse();
    }

    @Test
    public void categoryModelSetGetTest() throws Exception {
        CategoryModel unset = subject.getCategoryModel();
        assertThat(unset.title).isNullOrEmpty();
        assertThat(unset.index).isEqualTo(-1);
        assertThat(unset.icon).isEqualTo(-1);
        assertThat(unset.color).isEqualTo(-1);

        subject.setCategoryModel(getCategoryModel());
        CategoryModel categoryModel = subject.getCategoryModel();
        assertThat(categoryModel.title).isEqualTo("가고 싶은 곳");
        assertThat(categoryModel.index).isEqualTo(0);
        assertThat(categoryModel.icon).isEqualTo(SCHOOL.ordinal());
        assertThat(categoryModel.color).isEqualTo(ResourcesUtil.GREEN);
    }

    @Test
    public void getCategoryModelColorTest() throws Exception {
        subject.setCategoryModel(getCategoryModel());
        assertThat(subject.getCategoryModelColor()).isEqualTo(ResourcesUtil.GREEN);
    }

    @Test
    public void currentCardIndexSetGetTest() throws Exception {
        assertThat(subject.getCurrentCardIndex()).isEqualTo(0);
        subject.setCurrentCardIndex(2);
        assertThat(subject.getCurrentCardIndex()).isEqualTo(2);
    }

    private CategoryModel getCategoryModel() {
        return CategoryModel.builder()
                .title("가고 싶은 곳")
                .color(ResourcesUtil.GREEN)
                .icon(SCHOOL.ordinal())
                .index(0)
                .build();
    }
}