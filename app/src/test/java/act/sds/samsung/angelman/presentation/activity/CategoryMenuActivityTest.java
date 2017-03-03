package act.sds.samsung.angelman.presentation.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAbsListView;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowDrawable;

import java.util.ArrayList;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.custom.FontTextView;
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
    private CategoryMenuActivity subject;
    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    private TextView deleteButton;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList());
        subject = setupActivityWithIntent(CategoryMenuActivity.class, null);
        deleteButton = ((TextView) subject.findViewById(R.id.category_delete_button));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void whenLaunchedApplication_thenShowCategoryListByOrder() throws Exception {
        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        int size = categoryList.getChildCount();

        assertThat(((TextView) categoryList.getChildAt(0).findViewById(R.id.category_title)).getText()).isEqualTo("먹을 것");
        assertThat(((TextView) categoryList.getChildAt(1).findViewById(R.id.category_title)).getText()).isEqualTo("놀이");
        assertThat(((TextView) categoryList.getChildAt(2).findViewById(R.id.category_title)).getText()).isEqualTo("탈 것");
        assertThat(((TextView) categoryList.getChildAt(3).findViewById(R.id.category_title)).getText()).isEqualTo("가고 싶은 곳");

        if (size == 5) //TODO Device 호환성 코드 입력 후 GridView cardview를 크기 조정하면 마지막 아이템이 사라짐
            assertThat(((TextView) categoryList.getChildAt(4).findViewById(R.id.category_title)).getText()).isEqualTo("사람");

        assertThat(shadowOf(((ImageView) categoryList.getChildAt(0).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_food_menu);
        assertThat(shadowOf(((ImageView) categoryList.getChildAt(1).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_puzzle_menu);
        assertThat(shadowOf(((ImageView) categoryList.getChildAt(2).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_bus_menu);
        assertThat(shadowOf(((ImageView) categoryList.getChildAt(3).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_school_menu);

        if (size == 5)
            assertThat(shadowOf(((ImageView) categoryList.getChildAt(4).findViewById(R.id.category_icon)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_friend_menu);

        CardView cardView;
        RelativeLayout itemLayout;
        ShadowDrawable sd;

        cardView = (CardView) categoryList.getChildAt(0).findViewById(R.id.category_item_card);
        itemLayout = (RelativeLayout) categoryList.getChildAt(0).findViewById(R.id.category_item_layout);

        assertThat(cardView.getElevation() > 0).isTrue();
        sd = shadowOf(itemLayout.getBackground());
        assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_red);

        cardView = (CardView) categoryList.getChildAt(1).findViewById(R.id.category_item_card);
        itemLayout = (RelativeLayout) categoryList.getChildAt(1).findViewById(R.id.category_item_layout);

        assertThat(cardView.getElevation() > 0).isTrue();
        sd = shadowOf(itemLayout.getBackground());
        assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_orange);

        cardView = (CardView) categoryList.getChildAt(2).findViewById(R.id.category_item_card);
        itemLayout = (RelativeLayout) categoryList.getChildAt(2).findViewById(R.id.category_item_layout);

        assertThat(cardView.getElevation() > 0).isTrue();
        sd = shadowOf(itemLayout.getBackground());
        assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_yellow);

        cardView = (CardView) categoryList.getChildAt(3).findViewById(R.id.category_item_card);
        itemLayout = (RelativeLayout) categoryList.getChildAt(3).findViewById(R.id.category_item_layout);

        assertThat(cardView.getElevation() > 0).isTrue();
        sd = shadowOf(itemLayout.getBackground());
        assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_green);

        if (size == 5) {
            cardView = (CardView) categoryList.getChildAt(4).findViewById(R.id.category_item_card);
            itemLayout = (RelativeLayout) categoryList.getChildAt(4).findViewById(R.id.category_item_layout);

            assertThat(cardView.getElevation() > 0).isTrue();
            sd = shadowOf(itemLayout.getBackground());
            assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_blue);
        }

        if (size == 6) {
            itemLayout = (RelativeLayout) categoryList.getChildAt(5).findViewById(R.id.category_item_layout);
            sd = shadowOf(itemLayout.getBackground());
            assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.drop_shadow_dashgap);
            assertThat(itemLayout.getAlpha()).isEqualTo(0.7f);
        }
    }

    @Test
    public void givenHas5CategoryItem_whenLaunchedApplication_thenShowsAddCategoryButton() throws Exception {
        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        if (categoryList.getChildCount() == 6) {
            assertThat(((TextView) categoryList.getChildAt(5).findViewById(R.id.category_title)).getText()).isEqualTo("새 카테고리");
            ShadowDrawable siv = shadowOf(((ImageView) categoryList.getChildAt(5).findViewById(R.id.category_icon)).getDrawable());
            assertThat(siv.getCreatedFromResId()).isEqualTo(R.drawable.ic_add_category);
        }

    }

    @Test
    public void givenHasNoneCategoryItem_whenLaunchedApplication_thenShowsOnlyAddCategoryButton() throws Exception {
        when(categoryRepository.getCategoryAllList()).thenReturn(getNoneCategoryList());
        subject = setupActivity(CategoryMenuActivity.class);

        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        assertThat(((TextView) categoryList.getChildAt(0).findViewById(R.id.category_title)).getText()).isEqualTo("새 카테고리");
        ShadowDrawable sd = shadowOf(((ImageView) categoryList.getChildAt(0).findViewById(R.id.category_icon)).getDrawable());

        assertThat(sd.getCreatedFromResId()).isEqualTo(R.drawable.ic_add_category);
    }

    @Test
    public void givenHas6CategoryItem_whenLaunchedApplication_thenNotShowsAddCategoryButton() throws Exception {
        when(categoryRepository.getCategoryAllList()).thenReturn(getFullCategoryList());
        subject = setupActivity(CategoryMenuActivity.class);

        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        if (categoryList.getChildCount() == 6) {
            assertThat(((TextView) categoryList.getChildAt(5).findViewById(R.id.category_title)).getText()).isNotEqualTo("새 카테고리");
            ShadowDrawable sd = shadowOf(((ImageView) categoryList.getChildAt(5).findViewById(R.id.category_icon)).getDrawable());

            assertThat(sd.getCreatedFromResId()).isNotEqualTo(R.drawable.ic_add_category);
        }
    }


    @Test
    public void whenClickCategoryItem_thenStartActivityWithSelectedCategory() throws Exception {
        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        View view = categoryList.getChildAt(1);
        categoryList.performItemClick(view, 1, 0);

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(CardViewPagerActivity.class.getCanonicalName());
    }

    @Test
    public void givenDefaultMode_whenClickDeleteButton_thenCategoryMenuChangeToDeletable() throws Exception {
        deleteButton.performClick();

        assertThat(deleteButton.getText()).isEqualTo("완료");
    }

    @Test
    public void givenDeleteMode_whenClickDeleteButton_thenCategoryMenuChangeToDefault() throws Exception {
        deleteButton.performClick();
        deleteButton.performClick();

        assertThat(deleteButton.getText()).isEqualTo("삭제");
    }

    @Test
    public void givenDeleteMode_whenClickCategoryItem_thenShowAlertDialog() throws Exception {
        deleteButton.performClick();

        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

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
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        CategoryModel categoryModel1 = new CategoryModel();
        categoryModel1.title = "먹을 것";
        categoryModel1.color = ResourcesUtil.RED;
        categoryModel1.icon = FOOD.ordinal();
        categoryModel1.index = 0;
        categoryList.add(categoryModel1);

        when(categoryRepository.getCategoryAllList()).thenReturn(categoryList);
        subject = setupActivityWithIntent(CategoryMenuActivity.class, null);
        deleteButton = ((TextView) subject.findViewById(R.id.category_delete_button));

        deleteButton.performClick();

        GridView categoryListView = (GridView) subject.findViewById(R.id.category_list);

        ShadowAbsListView shadowCategoryList = shadowOf(categoryListView);
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
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        CategoryModel categoryModel1 = new CategoryModel();
        categoryModel1.title = "먹을 것";
        categoryModel1.color = ResourcesUtil.RED;
        categoryModel1.icon = FOOD.ordinal();
        categoryModel1.index = 0;
        categoryList.add(categoryModel1);

        when(categoryRepository.getCategoryAllList()).thenReturn(categoryList);
        subject = setupActivityWithIntent(CategoryMenuActivity.class, null);
        deleteButton = ((FontTextView) subject.findViewById(R.id.category_delete_button));

        deleteButton.performClick();

        GridView categoryListView = (GridView) subject.findViewById(R.id.category_list);

        ShadowAbsListView shadowCategoryList = shadowOf(categoryListView);
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
        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        ShadowAbsListView shadowCategoryList = shadowOf(categoryList);
        shadowCategoryList.populateItems();
        shadowCategoryList.performItemClick(5);

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(NewCategoryActivity.class.getCanonicalName());
    }

    @Test
    public void whenClickConfirmInDeleteAlertDialog_thenDeleteFilesInCategories() throws Exception {

        int removeCategoryIndex = 0;

        deleteButton.performClick();

        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        ShadowAbsListView shadowCategoryList = shadowOf(categoryList);
        shadowCategoryList.populateItems();
        shadowCategoryList.performItemClick(removeCategoryIndex);

        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();

        when(cardRepository.getSingleCardListWithCategoryId(removeCategoryIndex)).thenReturn(getCardListWithCategoryId());
        latestAlertDialog.findViewById(R.id.confirm).performClick();

        verify(cardRepository).deleteSingleCardsWithCategory(removeCategoryIndex);
        verify(categoryRepository).deleteCategory(removeCategoryIndex);
    }

    private ArrayList<CardModel> getCardListWithCategoryId() {
        ArrayList<CardModel> ret = new ArrayList<>();
        addSingleCardModel(ret, "이미지", "DCIM/image.png", "20013928_120015");
        addSingleCardModel(ret, "물", "water.png", "20010928_120020");
        addSingleCardModel(ret, "우유", "milk.png", "20010928_120019");
        addSingleCardModel(ret, "쥬스", "juice.png", "20010928_120015");
        return ret;
    }

    private void addSingleCardModel(ArrayList list, String name, String path, String time) {
        CardModel model = new CardModel(name, path, time);
        list.add(model);
    }

    private ArrayList<CategoryModel> getFullCategoryList() {
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        CategoryModel categoryModel1 = new CategoryModel();
        categoryModel1.title = "먹을 것";
        categoryModel1.color = ResourcesUtil.RED;
        categoryModel1.icon = FOOD.ordinal();
        categoryModel1.index = 0;

        CategoryModel categoryModel2 = new CategoryModel();
        categoryModel2.title = "놀이";
        categoryModel2.color = ResourcesUtil.ORANGE;
        categoryModel2.icon = PUZZLE.ordinal();
        categoryModel2.index = 1;

        CategoryModel categoryModel3 = new CategoryModel();
        categoryModel3.title = "탈 것";
        categoryModel3.color = ResourcesUtil.YELLOW;
        categoryModel3.icon = BUS.ordinal();
        categoryModel3.index = 2;

        CategoryModel categoryModel4 = new CategoryModel();
        categoryModel4.title = "가고 싶은 곳";
        categoryModel4.color = ResourcesUtil.GREEN;
        categoryModel4.icon = SCHOOL.ordinal();
        categoryModel4.index = 3;

        CategoryModel categoryModel5 = new CategoryModel();
        categoryModel5.title = "사람";
        categoryModel5.color = ResourcesUtil.BLUE;
        categoryModel5.icon = FRIEND.ordinal();
        categoryModel5.index = 4;

        CategoryModel categoryModel6 = new CategoryModel();
        categoryModel6.title = "엄마";
        categoryModel6.color = ResourcesUtil.BLUE;
        categoryModel6.icon = FRIEND.ordinal();
        categoryModel6.index = 5;

        categoryList.add(categoryModel1);
        categoryList.add(categoryModel2);
        categoryList.add(categoryModel3);
        categoryList.add(categoryModel4);
        categoryList.add(categoryModel5);
        categoryList.add(categoryModel6);

        return categoryList;
    }

    private ArrayList<CategoryModel> getNoneCategoryList() {
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        return categoryList;
    }

    private ArrayList<CategoryModel> getCategoryList() {
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        CategoryModel categoryModel1 = new CategoryModel();
        categoryModel1.title = "먹을 것";
        categoryModel1.color = ResourcesUtil.RED;
        categoryModel1.icon = FOOD.ordinal();
        categoryModel1.index = 0;

        CategoryModel categoryModel2 = new CategoryModel();
        categoryModel2.title = "놀이";
        categoryModel2.color = ResourcesUtil.ORANGE;
        categoryModel2.icon = PUZZLE.ordinal();
        categoryModel2.index = 1;

        CategoryModel categoryModel3 = new CategoryModel();
        categoryModel3.title = "탈 것";
        categoryModel3.color = ResourcesUtil.YELLOW;
        categoryModel3.icon = BUS.ordinal();
        categoryModel3.index = 2;

        CategoryModel categoryModel4 = new CategoryModel();
        categoryModel4.title = "가고 싶은 곳";
        categoryModel4.color = ResourcesUtil.GREEN;
        categoryModel4.icon = SCHOOL.ordinal();
        categoryModel4.index = 3;

        CategoryModel categoryModel5 = new CategoryModel();
        categoryModel5.title = "사람";
        categoryModel5.color = ResourcesUtil.BLUE;
        categoryModel5.icon = FRIEND.ordinal();
        categoryModel5.index = 4;

        categoryList.add(categoryModel1);
        categoryList.add(categoryModel2);
        categoryList.add(categoryModel3);
        categoryList.add(categoryModel4);
        categoryList.add(categoryModel5);
        return categoryList;
    }
}