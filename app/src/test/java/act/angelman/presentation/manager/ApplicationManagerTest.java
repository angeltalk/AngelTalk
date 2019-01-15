package act.angelman.presentation.manager;


import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import act.angelman.domain.model.CategoryModel;
import act.angelman.presentation.custom.ChildModeManager;
import act.angelman.presentation.shadow.ShadowKakaoLink;
import act.angelman.presentation.shadow.ShadowKeyCharacterMap;
import act.angelman.presentation.util.ResourcesUtil;

import static act.angelman.presentation.util.ResourceMapper.IconType.SCHOOL;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22, shadows = {ShadowKakaoLink.class, ShadowKeyCharacterMap.class})
public class ApplicationManagerTest {

    private ApplicationManager subject;
    private static final String CHILD_MODE = "childMode";

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

    @Test
    public void setChildModeTest() throws Exception {
        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isFalse();
        subject.setChildMode();
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isTrue();
    }

    @Test
    public void setNotChildModeTest() throws Exception {
        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isFalse();
        subject.setChildMode();
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isTrue();
        subject.setNotChildMode();
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, true)).isFalse();
    }

    @Test
    public void isChildModeTest() throws Exception {
        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(subject.isChildMode()).isEqualTo(sharedPreferences.getBoolean(CHILD_MODE, true));
        assertThat(subject.isChildMode()).isTrue();
        subject.setNotChildMode();
        assertThat(subject.isChildMode()).isEqualTo(sharedPreferences.getBoolean(CHILD_MODE, true));
        assertThat(subject.isChildMode()).isFalse();
    }

    @Test
    public void changeChildModeTest() throws Exception {
        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isFalse();
        subject.changeChildMode(true);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isTrue();
        subject.changeChildMode(false);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, true)).isFalse();
    }

    @Test
    public void childModeManagerCreateCategoryMenuTest() throws  Exception{
        ChildModeManager childModeManager = new ChildModeManager(RuntimeEnvironment.application.getApplicationContext());
        Field field = ChildModeManager.class.getDeclaredField("categoryMenuLayout");
        field.setAccessible(true);
        assertThat(childModeManager.getCategoryMenuLayout()).isNull();
        childModeManager.createAndAddCategoryMenu();
        assertThat(childModeManager.getCategoryMenuLayout()).isNotNull();
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