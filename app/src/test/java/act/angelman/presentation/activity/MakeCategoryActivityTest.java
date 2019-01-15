package act.angelman.presentation.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.List;

import javax.inject.Inject;

import act.angelman.BuildConfig;
import act.angelman.R;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CategoryItemModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CategoryRepository;
import act.angelman.presentation.adapter.NewCategoryItemAdapter;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.util.ResourceMapper;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class MakeCategoryActivityTest extends UITest {

    private MakeCategoryActivity subject;
    private EditText editCategoryTitle;
    private ImageView saveButton;
    private ImageView cancelButton;
    private TextView categoryTitle;

    @Inject
    CategoryRepository repository;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);

        initIconList();
        initColorList();

        subject = setupActivity(MakeCategoryActivity.class);
        editCategoryTitle = (EditText) subject.findViewById(R.id.edit_category_title);
        saveButton = (ImageView) subject.findViewById(R.id.new_category_save_button);
        cancelButton = (ImageView) subject.findViewById(R.id.category_title_cancel);
        categoryTitle = (TextView) subject.findViewById(R.id.category_title);
    }

    @Test
    public void whenDoneCategoryTitleEdit_thenApplyAtPreviewCategoryItem() throws Exception {
        editCategoryTitle.setText("test");
        editCategoryTitle.onEditorAction(EditorInfo.IME_ACTION_DONE);

        TextView previewCategoryTitle = (TextView) subject.findViewById(R.id.category_title);
        assertThat(previewCategoryTitle.getText().toString()).isEqualTo("test");


        editCategoryTitle.setText("");
        editCategoryTitle.onEditorAction(EditorInfo.IME_ACTION_DONE);
        assertThat(previewCategoryTitle.getText().toString()).isEqualTo("카테고리 이름");
    }

    @Test
    public void whenClickLeftArrowButton_finishActivity() throws Exception {
        ImageView leftArrowButton = (ImageView) subject.findViewById(R.id.left_arrow_button);

        leftArrowButton.performClick();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void whenBackPress_finishActivity() throws Exception {
        subject.onBackPressed();
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void whenLaunchedActivity_thenShowsNewCategoryNameInPreview() throws Exception {
        TextView categoryTitle = (TextView) subject.findViewById(R.id.category_title);

        assertThat(categoryTitle.getText()).isEqualTo("카테고리 이름");
    }

    @Test
    public void whenLaunchedActivityAndNoTextInEditBox_thenNotShowingCancelButton() throws Exception {
        assertThat(cancelButton).isInvisible();
    }

    @Test
    public void whenEnterCategoryTitle_thenEnableSaveButtonAndShowingCancelButton() throws Exception {
        assertThat(saveButton.isEnabled()).isFalse();
        editCategoryTitle.setText("category name");
        assertThat(saveButton.isEnabled()).isTrue();
        assertThat(cancelButton).isVisible();
    }

    @Test
    public void whenEnterCategoryEdit_thenShowsTheTextImmediately() throws Exception {
        editCategoryTitle.setText("1");
        assertThat(categoryTitle.getText().toString()).isEqualTo("1");
        editCategoryTitle.setText("12");
        assertThat(categoryTitle.getText().toString()).isEqualTo("12");
        editCategoryTitle.setText("123");
        assertThat(categoryTitle.getText().toString()).isEqualTo("123");
    }


    @Test
    public void givenExistCategoryTitle_whenClickedCategoryTitleCancelButton_thenRemoveAllTextInEditTextAndNotShowsCancleButton() throws Exception {
        assertThat(cancelButton).isInvisible();
        editCategoryTitle.setText("category name");
        assertThat(cancelButton).isVisible();

        cancelButton.performClick();
        assertThat(cancelButton).isInvisible();
        assertThat(editCategoryTitle.getText().toString()).isEqualTo("");
        assertThat(categoryTitle.getText().toString()).isEqualTo("카테고리 이름");
    }

    @Test
    public void whenClickSaveButton_thenCallSaveNewCategoryItemAndReturnId() throws Exception {
        editCategoryTitle.setText("category name");
        when(repository.saveNewCategoryItemAndReturnId((CategoryModel) anyObject())).thenReturn(1);
        saveButton.performClick();
        verify(repository).saveNewCategoryItemAndReturnId((CategoryModel) anyObject());
    }

    @Test
    public void whenClickSaveButton_thenShowCardViewPagerWithAddCategoryItem() throws Exception {
        editCategoryTitle.setText("category name");
        when(repository.saveNewCategoryItemAndReturnId((CategoryModel) anyObject())).thenReturn(1);
        saveButton.performClick();

        Intent actualIntent = shadowOf(subject).getNextStartedActivity();
        int actualColor = (int) actualIntent.getExtras().get(ApplicationConstants.CATEGORY_COLOR);

        assertThat(actualColor).isEqualTo(ResourceMapper.ColorType.RED.ordinal());
    }

    @Test
    public void whenSuccessSaveNewCategory_thenFinishActivity() throws Exception {
        editCategoryTitle.setText("category name");
        when(repository.saveNewCategoryItemAndReturnId((CategoryModel) anyObject())).thenReturn(1);
        saveButton.performClick();

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void whenLaunchedActivity_thenShowsIconListCorrectlyAndShowsDefaultPreview() throws Exception {
        RecyclerView iconList = (RecyclerView) subject.findViewById(R.id.icon_list);
        assertThat(iconList.getAdapter()).isNotNull();

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        NewCategoryItemAdapter adapter = (NewCategoryItemAdapter) iconList.getAdapter();
        assertThat(adapter.getItemCount()).isEqualTo(4);

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_hospital_select);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_puzzle_used);

        final ImageView previewIconImage = (ImageView) subject.findViewById(R.id.category_icon);
        assertThat(shadowOf(previewIconImage.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_hospital_menu);
    }

    @Test
    public void whenClickedIconList_thenChangedIconStateAndShowsInPreview() throws Exception {
        RecyclerView iconList = (RecyclerView) subject.findViewById(R.id.icon_list);

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        final int selectIndex = 2;
        iconList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_hospital_unselect);

        NewCategoryItemAdapter.NewCategoryItemViewHolder selectHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(selectIndex);
        assertThat(shadowOf(selectHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_friend_select);

        final ImageView previewIconImage = (ImageView) subject.findViewById(R.id.category_icon);
        assertThat(shadowOf(previewIconImage.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_friend_menu);
    }

    @Test
    public void whenClickedUsedIconInList_thenNothingToDo() throws Exception {
        RecyclerView iconList = (RecyclerView) subject.findViewById(R.id.icon_list);

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        final int selectIndex = 3;
        iconList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_hospital_select);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_puzzle_used);
    }

    @Test
    public void whenLaunchedActivity_thenShowsColorListCorrectly() throws Exception {
        RecyclerView colorList = (RecyclerView) subject.findViewById(R.id.color_list);
        assertThat(colorList.getAdapter()).isNotNull();

        colorList.measure(0, 0);
        colorList.layout(0, 0, 10000, 100);

        NewCategoryItemAdapter adapter = (NewCategoryItemAdapter) colorList.getAdapter();
        assertThat(adapter.getItemCount()).isEqualTo(4);

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_color_red_select);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_color_yellow_used);

        final RelativeLayout previewColorLayout = (RelativeLayout) subject.findViewById(R.id.new_category_color);
        assertThat(shadowOf(previewColorLayout.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_red);
    }

    @Test
    public void whenClickedColorList_thenChangedColorStateAndShowsInPreview() throws Exception {
        RecyclerView colorList = (RecyclerView) subject.findViewById(R.id.color_list);
        assertThat(colorList.getAdapter()).isNotNull();

        colorList.measure(0, 0);
        colorList.layout(0, 0, 10000, 100);

        final int selectIndex = 1;
        colorList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_color_red_unselect);

        NewCategoryItemAdapter.NewCategoryItemViewHolder selectHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(selectIndex);
        assertThat(shadowOf(selectHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_color_blue_select);

        final RelativeLayout previewColorLayout = (RelativeLayout) subject.findViewById(R.id.new_category_color);
        assertThat(shadowOf(previewColorLayout.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_blue);
    }

    @Test
    public void whenClickedUsedColorInList_thenNothingToDo() throws Exception {
        RecyclerView colorList = (RecyclerView) subject.findViewById(R.id.color_list);
        assertThat(colorList.getAdapter()).isNotNull();

        colorList.measure(0, 0);
        colorList.layout(0, 0, 10000, 100);

        final int selectIndex = 3;
        colorList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_color_red_select);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder = (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.icon_color_yellow_used);
    }

    @Test
    public void givenTextDataChange_whenBackPress_showAlertDialog() throws Exception {
        editCategoryTitle.setText("test");
        subject.onBackPressed();

        assertThat(ShadowAlertDialog.getLatestAlertDialog().isShowing()).isTrue();
    }

    @Test
    public void givenAlertDialogIsShowing_whenPositiveButtonPress_finishActivity() throws Exception {
        editCategoryTitle.setText("test");
        subject.onBackPressed();

        ShadowAlertDialog.getLatestAlertDialog().findViewById(R.id.confirm_button).performClick();
        assertThat(subject.isFinishing()).isTrue();
    }


    @Test
    public void givenIconDataChange_whenBackPress_showAlertDialog() throws Exception {
        RecyclerView iconList = (RecyclerView) subject.findViewById(R.id.icon_list);

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        final int selectIndex = 0;
        iconList.getChildAt(selectIndex).performClick();

        subject.onBackPressed();

        ShadowAlertDialog.getLatestAlertDialog().findViewById(R.id.confirm_button).performClick();
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void givenAlertDialogIsShowing_whenNegativeButtonPress_finishActivity() throws Exception {
        editCategoryTitle.setText("test");
        subject.onBackPressed();

        ShadowAlertDialog.getLatestAlertDialog().getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
        assertThat(subject.isFinishing()).isFalse();
        assertThat(editCategoryTitle.getText().toString()).isEqualTo("test");
    }

    @Test
    public void whenCategoryNameIsOver12ChractersInEnglish_thenDeleteAfter12thCharacters() throws Exception {
        String LENGTH_13_ENGLISH_STRING = "abcdefghijklm";
        subject.editCategoryTitle.setText(LENGTH_13_ENGLISH_STRING);

        assertThat(subject.editCategoryTitle.getText().toString()).isEqualTo("abcdefghijkl");
        assertThat(subject.editCategoryTitle.getText().length()).isEqualTo(12);
    }

    @Test
    public void whenCategoryNameIsOver6ChractersInKorean_thenDeleteAfter6thCharacters() throws Exception {
        String LENGTH_10_KOREAN_STRING = "일이삼사오육칠팔구십";
        subject.editCategoryTitle.setText(LENGTH_10_KOREAN_STRING);

        assertThat(subject.editCategoryTitle.getText().toString()).isEqualTo("일이삼사오육");
        assertThat(subject.editCategoryTitle.getText().length()).isEqualTo(6);
    }

    private void initIconList() {
        List<CategoryItemModel> mockItemList = Lists.newArrayList();

        CategoryItemModel mock1 = new CategoryItemModel();
        mock1.status = ResourceMapper.IconState.DEFAULT.ordinal();
        mock1.type = ResourceMapper.IconType.HOSPITAL.ordinal();

        CategoryItemModel mock2 = new CategoryItemModel();
        mock2.status = ResourceMapper.IconState.DEFAULT.ordinal();
        mock2.type = ResourceMapper.IconType.DOG.ordinal();

        CategoryItemModel mock3 = new CategoryItemModel();
        mock3.status = ResourceMapper.IconState.DEFAULT.ordinal();
        mock3.type = ResourceMapper.IconType.FRIEND.ordinal();

        CategoryItemModel mock4 = new CategoryItemModel();
        mock4.status = ResourceMapper.IconState.USED.ordinal();
        mock4.type = ResourceMapper.IconType.PUZZLE.ordinal();

        mockItemList.add(mock1);
        mockItemList.add(mock2);
        mockItemList.add(mock3);
        mockItemList.add(mock4);

        when(repository.getCategoryAllIconList()).thenReturn(mockItemList);
    }

    private void initColorList() {
        List<CategoryItemModel> mockItemList = Lists.newArrayList();

        CategoryItemModel mock1 = new CategoryItemModel();
        mock1.status = ResourceMapper.ColorState.MENU.ordinal();
        mock1.type = ResourceMapper.ColorType.RED.ordinal();

        CategoryItemModel mock2 = new CategoryItemModel();
        mock2.status = ResourceMapper.ColorState.MENU.ordinal();
        mock2.type = ResourceMapper.ColorType.BLUE.ordinal();

        CategoryItemModel mock3 = new CategoryItemModel();
        mock3.status = ResourceMapper.ColorState.MENU.ordinal();
        mock3.type = ResourceMapper.ColorType.PURPLE.ordinal();

        CategoryItemModel mock4 = new CategoryItemModel();
        mock4.status = ResourceMapper.ColorState.USED.ordinal();
        mock4.type = ResourceMapper.ColorType.YELLOW.ordinal();

        mockItemList.add(mock1);
        mockItemList.add(mock2);
        mockItemList.add(mock3);
        mockItemList.add(mock4);

        when(repository.getCategoryAllBackgroundList()).thenReturn(mockItemList);
    }
}