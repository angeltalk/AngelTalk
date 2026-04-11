package angeltalk.plus.presentation.manager;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.presentation.custom.ChildModeManager;
import angeltalk.plus.presentation.shadow.ShadowKeyCharacterMap;
import angeltalk.plus.presentation.util.ResourcesUtil;

import static angeltalk.plus.presentation.util.ResourceMapper.IconType.SCHOOL;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowKeyCharacterMap.class})
public class ApplicationManagerTest {

    private ApplicationManager subject;
    private static final String CHILD_MODE = "childMode";

    @Before
    public void setUp() throws Exception {
        subject = new ApplicationManager(((Context) ApplicationProvider.getApplicationContext()).getApplicationContext());
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
        SharedPreferences sharedPreferences = ((Context) ApplicationProvider.getApplicationContext()).getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isFalse();
        subject.setChildMode();
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isTrue();
    }

    @Test
    public void setNotChildModeTest() throws Exception {
        SharedPreferences sharedPreferences = ((Context) ApplicationProvider.getApplicationContext()).getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isFalse();
        subject.setChildMode();
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isTrue();
        subject.setNotChildMode();
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, true)).isFalse();
    }

    @Test
    public void isChildModeTest() throws Exception {
        SharedPreferences sharedPreferences = ((Context) ApplicationProvider.getApplicationContext()).getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(subject.isChildMode()).isEqualTo(sharedPreferences.getBoolean(CHILD_MODE, true));
        assertThat(subject.isChildMode()).isTrue();
        subject.setNotChildMode();
        assertThat(subject.isChildMode()).isEqualTo(sharedPreferences.getBoolean(CHILD_MODE, true));
        assertThat(subject.isChildMode()).isFalse();
    }

    @Test
    public void changeChildModeTest() throws Exception {
        SharedPreferences sharedPreferences = ((Context) ApplicationProvider.getApplicationContext()).getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isFalse();
        subject.changeChildMode(true);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, false)).isTrue();
        subject.changeChildMode(false);
        assertThat(sharedPreferences.getBoolean(CHILD_MODE, true)).isFalse();
    }

    // TODO(phase-9): CategoryMenuLayout is a @AndroidEntryPoint view that requires an
    // Activity/Fragment context for Hilt injection; this test passes the Application
    // context, which fails under Hilt. Re-enable by wiring a HiltAndroidRule + a host
    // Activity built via Robolectric.
    @Ignore
    @Test
    public void childModeManagerCreateCategoryMenuTest() throws Exception {
        ChildModeManager childModeManager = new ChildModeManager(((Context) ApplicationProvider.getApplicationContext()).getApplicationContext());
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