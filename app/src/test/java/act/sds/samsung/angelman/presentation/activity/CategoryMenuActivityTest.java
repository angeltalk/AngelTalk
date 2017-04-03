package act.sds.samsung.angelman.presentation.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAbsListView;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowDrawable;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.network.transfer.CardTransfer;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.BUS;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.FOOD;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.FRIEND;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.PUZZLE;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.SCHOOL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CategoryMenuActivityTest extends UITest {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    CardTransfer cardTransfer;

    private CategoryMenuActivity subject;
    private GridView categoryList;
    private TextView categoryDeleteButton;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        setUpActivityWithCategoryList(5);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void whenLaunchedApplication_thenShowCategoryListByOrder() throws Exception {

        int size = categoryList.getChildCount();

        assertThat(((TextView) categoryList.getChildAt(0).findViewById(R.id.category_title)).getText()).isEqualTo("먹을 것");
        assertThat(((TextView) categoryList.getChildAt(1).findViewById(R.id.category_title)).getText()).isEqualTo("놀이");
        assertThat(((TextView) categoryList.getChildAt(2).findViewById(R.id.category_title)).getText()).isEqualTo("탈 것");
        assertThat(((TextView) categoryList.getChildAt(3).findViewById(R.id.category_title)).getText()).isEqualTo("가고 싶은 곳");

        if (size == 5) {//TODO Device 호환성 코드 입력 후 GridView cardview를 크기 조정하면 마지막 아이템이 사라짐
            assertThat(((TextView) categoryList.getChildAt(4).findViewById(R.id.category_title)).getText()).isEqualTo("사람");
        }

        assertThat(shadowOf(((ImageView) categoryList.getChildAt(0).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_food_menu);
        assertThat(shadowOf(((ImageView) categoryList.getChildAt(1).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_puzzle_menu);
        assertThat(shadowOf(((ImageView) categoryList.getChildAt(2).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_bus_menu);
        assertThat(shadowOf(((ImageView) categoryList.getChildAt(3).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_school_menu);

        if (size == 5) {
            assertThat(shadowOf(((ImageView) categoryList.getChildAt(4).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_friend_menu);
        }

        assertCardViewBackgroundColor(categoryList, 0, R.drawable.background_gradient_red);
        assertCardViewBackgroundColor(categoryList, 1, R.drawable.background_gradient_orange);
        assertCardViewBackgroundColor(categoryList, 2, R.drawable.background_gradient_yellow);
        assertCardViewBackgroundColor(categoryList, 3, R.drawable.background_gradient_green);

        if (size == 5) {
            assertCardViewBackgroundColor(categoryList, 4, R.drawable.background_gradient_blue);
        }

        if (size == 6) {
            RelativeLayout itemLayout;
            ShadowDrawable sd;

            itemLayout = (RelativeLayout) categoryList.getChildAt(5).findViewById(R.id.category_item_layout);
            sd = shadowOf(itemLayout.getBackground());
            assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.drop_shadow_dashgap);
            assertThat(itemLayout.getAlpha()).isEqualTo(0.7f);
        }
    }

    @Test
    public void givenHas5CategoryItem_whenLaunchedApplication_thenShowsAddCategoryButton() throws Exception {
        if (categoryList.getChildCount() == 6) {
            assertThat(((TextView) categoryList.getChildAt(5).findViewById(R.id.category_title)).getText()).isEqualTo("새 카테고리");
            ShadowDrawable siv = shadowOf(((ImageView) categoryList.getChildAt(5).findViewById(R.id.category_icon)).getDrawable());
            assertThat(siv.getCreatedFromResId()).isEqualTo(R.drawable.ic_add_category);
        }
    }

    @Test
    public void givenHasNoneCategoryItem_whenLaunchedApplication_thenShowsOnlyAddCategoryButton() throws Exception {
        setUpActivityWithCategoryList(0);

        assertThat(((TextView) categoryList.getChildAt(0).findViewById(R.id.category_title)).getText()).isEqualTo("새 카테고리");
        ShadowDrawable sd = shadowOf(((ImageView) categoryList.getChildAt(0).findViewById(R.id.category_icon)).getDrawable());

        assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.ic_add_category);
    }

    @Test
    public void givenHas6CategoryItem_whenLaunchedApplication_thenNotShowsAddCategoryButton() throws Exception {
        setUpActivityWithCategoryList(6);

        assertThat(categoryList.getChildCount()).isEqualTo(6);
        assertThat(((TextView) categoryList.getChildAt(5).findViewById(R.id.category_title)).getText()).isNotEqualTo("새 카테고리");
        ShadowDrawable sd = shadowOf(((ImageView) categoryList.getChildAt(5).findViewById(R.id.category_icon)).getDrawable());
        assertThat(sd.getCreatedFromResId()).isNotEqualTo(R.drawable.ic_add_category);

    }

    @Test
    public void whenClickCategoryItem_thenStartActivityWithSelectedCategory() throws Exception {
        categoryList.performItemClick(categoryList.getChildAt(1), 1, 0);

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(CardViewPagerActivity.class.getCanonicalName());
    }

    @Test
    public void givenDefaultMode_whenClickDeleteButton_thenCategoryMenuChangeToDeletable() throws Exception {
        subject.categoryDeleteButton.performClick();

        assertThat(subject.categoryDeleteButton.getText()).isEqualTo("완료");
    }

    @Test
    public void givenDeleteMode_whenClickDeleteButton_thenCategoryMenuChangeToDefault() throws Exception {
        subject.categoryDeleteButton.performClick();
        subject.categoryDeleteButton.performClick();

        assertThat(subject.categoryDeleteButton.getText()).isEqualTo("삭제");
    }

    @Test
    public void givenDeleteMode_whenBackPressed_thenCategoryMenuChangeToDefault() throws Exception {
        subject.categoryDeleteButton.performClick();
        subject.onBackPressed();

        assertThat(subject.categoryDeleteButton.getText()).isEqualTo("삭제");
    }

    @Test
    public void givenDeleteMode_whenClickCategoryItem_thenShowAlertDialog() throws Exception {
        subject.categoryDeleteButton.performClick();

        ShadowAbsListView shadowCategoryList = shadowOf(categoryList);
        shadowCategoryList.populateItems();
        shadowCategoryList.performItemClick(0);

        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(latestAlertDialog).isNotNull();
        assertThat(((TextView) latestAlertDialog.findViewById(R.id.confirm)).getText()).isEqualTo("확인");
        assertThat(((TextView) latestAlertDialog.findViewById(R.id.cancel)).getText()).isEqualTo("취소");
        assertThat((((TextView) latestAlertDialog.findViewById(R.id.alert_message)).getText())).contains("먹을 것");
    }

    @Test
    public void givenDeleteModeOnlyOneCategoryItemIsLeft_whenClickCategoryItem_thenShowOtherAlertDialogMessage() throws Exception {
        setUpActivityWithCategoryList(1);

        categoryDeleteButton.performClick();

        ShadowAbsListView shadowCategoryList = shadowOf(categoryList);
        shadowCategoryList.populateItems();
        shadowCategoryList.performItemClick(0);

        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(latestAlertDialog).isNotNull();
        assertThat(((TextView) latestAlertDialog.findViewById(R.id.confirm)).getText()).isEqualTo("확인");
        assertThat(((TextView) latestAlertDialog.findViewById(R.id.cancel)).getText()).isEqualTo("취소");
        assertThat(((TextView) latestAlertDialog.findViewById(R.id.alert_message)).getText()).isEqualTo("카테고리는 최소 1개 이상이어야 합니다. 삭제 후 새 카테고리를 만드시겠습니까?");
    }

    @Test
    public void whenLastCategoryMenuDeleted_thenLaunchNewCategoryActivity() throws Exception {
        setUpActivityWithCategoryList(1);

        categoryDeleteButton.performClick();

        ShadowAbsListView shadowCategoryList = shadowOf(categoryList);
        shadowCategoryList.populateItems();
        shadowCategoryList.performItemClick(0);

        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        latestAlertDialog.findViewById(R.id.confirm).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(NewCategoryActivity.class.getCanonicalName());
    }

    @Test
    public void whenClickNewCategoryItem_thenLaunchesNewCategoryActivity() throws Exception {
        ShadowAbsListView shadowCategoryList = shadowOf(categoryList);
        shadowCategoryList.populateItems();
        shadowCategoryList.performItemClick(5);

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(NewCategoryActivity.class.getCanonicalName());
    }

    @Test
    public void whenClickConfirmInDeleteAlertDialog_thenDeleteFilesInCategories() throws Exception {
        subject.categoryDeleteButton.performClick();

        ShadowAbsListView shadowCategoryList = shadowOf(categoryList);
        shadowCategoryList.populateItems();
        shadowCategoryList.performItemClick(0);

        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        latestAlertDialog.findViewById(R.id.confirm).performClick();

        verify(cardRepository).deleteSingleCardsWithCategory(0);
        verify(categoryRepository).deleteCategory(0);
    }

    @Test
    public void givenCategoryListIsChangedAfterLaunched_whenOnResume_thenShowCategoryListCorrectly() throws Exception {
        // given
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList(6));
        // when
        subject.onResume();
        // then
        assertThat(categoryList.getChildCount()).isEqualTo(6);
    }

    @Test
    public  void whenKaKaoIntentReceived_thenShowDownloadConfirmPopup() throws Exception{
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme)+"://"+ getString(R.string.kakaolink_host)));
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList(5));
        ActivityController ac = Robolectric.buildActivity(CategoryMenuActivity.class).withIntent(intent).create();
        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(latestAlertDialog).isNotNull();
    }

    private void setUpActivityWithCategoryList(int listSize) {
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList(listSize));
        subject = setupActivity(CategoryMenuActivity.class);
        categoryList = subject.categoryGridView;
        categoryDeleteButton = subject.categoryDeleteButton;
    }

    private void assertCardViewBackgroundColor(GridView categoryList, int index, int id) {
        CardView cardView;
        RelativeLayout itemLayout;
        ShadowDrawable sd;

        cardView = (CardView) categoryList.getChildAt(index).findViewById(R.id.category_item_card);
        itemLayout = (RelativeLayout) categoryList.getChildAt(index).findViewById(R.id.category_item_layout);

        assertThat(cardView.getElevation() > 0).isTrue();
        sd = shadowOf(itemLayout.getBackground());
        assertThat(sd.getCreatedFromResId()).isEqualTo(id);
    }

    private List<CategoryModel> getCategoryList(int listSize) {
        List<CategoryModel> categoryList = new ArrayList<>();
        CategoryModel [] categoryModel = new CategoryModel[listSize];

        switch(listSize) {
            case 6: //Full
                categoryModel[5] = new CategoryModel();
                categoryModel[5].title = "엄마";
                categoryModel[5].color = ResourcesUtil.BLUE;
                categoryModel[5].icon = FRIEND.ordinal();
                categoryModel[5].index = 5;
            case 5: //Default
                categoryModel[4] = new CategoryModel();
                categoryModel[4].title = "사람";
                categoryModel[4].color = ResourcesUtil.BLUE;
                categoryModel[4].icon = FRIEND.ordinal();
                categoryModel[4].index = 4;

                categoryModel[3] = new CategoryModel();
                categoryModel[3].title = "가고 싶은 곳";
                categoryModel[3].color = ResourcesUtil.GREEN;
                categoryModel[3].icon = SCHOOL.ordinal();
                categoryModel[3].index = 3;

                categoryModel[2] = new CategoryModel();
                categoryModel[2].title = "탈 것";
                categoryModel[2].color = ResourcesUtil.YELLOW;
                categoryModel[2].icon = BUS.ordinal();
                categoryModel[2].index = 2;

                categoryModel[1] = new CategoryModel();
                categoryModel[1].title = "놀이";
                categoryModel[1].color = ResourcesUtil.ORANGE;
                categoryModel[1].icon = PUZZLE.ordinal();
                categoryModel[1].index = 1;
            case 1: //One
                categoryModel[0] = new CategoryModel();
                categoryModel[0].title = "먹을 것";
                categoryModel[0].color = ResourcesUtil.RED;
                categoryModel[0].icon = FOOD.ordinal();
                categoryModel[0].index = 0;
            default: //None
        }

        for (CategoryModel model : categoryModel) {
            categoryList.add(model);
        }

        return categoryList;
    }
}