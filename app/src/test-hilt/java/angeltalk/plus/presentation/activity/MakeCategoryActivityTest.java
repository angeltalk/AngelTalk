package angeltalk.plus.presentation.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.List;

import angeltalk.plus.R;
import angeltalk.plus.UITest;
import angeltalk.plus.dagger.modules.AngelmanModule;
import angeltalk.plus.domain.model.CategoryItemModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CategoryRepository;
import angeltalk.plus.presentation.adapter.NewCategoryItemAdapter;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.util.ResourceMapper;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@HiltAndroidTest
@UninstallModules(AngelmanModule.class)
@RunWith(RobolectricTestRunner.class)
public class MakeCategoryActivityTest extends UITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    CategoryRepository repository = Mockito.mock(CategoryRepository.class);

    @BindValue
    angeltalk.plus.domain.repository.CardRepository cardRepository =
            Mockito.mock(angeltalk.plus.domain.repository.CardRepository.class);

    private MakeCategoryActivity subject;
    private EditText editCategoryTitle;
    private ImageView saveButton;
    private ImageView cancelButton;
    private TextView categoryTitle;

    @Before
    public void setUp() throws Exception {
        hiltRule.inject();

        initIconList();
        initColorList();

        subject = setupActivity(MakeCategoryActivity.class);
        editCategoryTitle = subject.findViewById(R.id.edit_category_title);
        saveButton = subject.findViewById(R.id.new_category_save_button);
        cancelButton = subject.findViewById(R.id.category_title_cancel);
        categoryTitle = subject.findViewById(R.id.category_title);
    }

    @Test
    public void whenDoneCategoryTitleEdit_thenApplyAtPreviewCategoryItem() throws Exception {
        editCategoryTitle.setText("test");
        editCategoryTitle.onEditorAction(EditorInfo.IME_ACTION_DONE);

        TextView previewCategoryTitle = subject.findViewById(R.id.category_title);
        assertThat(previewCategoryTitle.getText().toString()).isEqualTo("test");

        editCategoryTitle.setText("");
        editCategoryTitle.onEditorAction(EditorInfo.IME_ACTION_DONE);
        assertThat(previewCategoryTitle.getText().toString()).isEqualTo("카테고리 이름");
    }

    @Test
    public void whenClickLeftArrowButton_finishActivity() throws Exception {
        ImageView leftArrowButton = subject.findViewById(R.id.left_arrow_button);

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
        TextView categoryTitle = subject.findViewById(R.id.category_title);

        assertThat(categoryTitle.getText().toString()).isEqualTo("카테고리 이름");
    }

    @Test
    public void whenLaunchedActivityAndNoTextInEditBox_thenNotShowingCancelButton() throws Exception {
        assertThat(cancelButton.getVisibility()).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void whenEnterCategoryTitle_thenEnableSaveButtonAndShowingCancelButton() throws Exception {
        assertThat(saveButton.isEnabled()).isFalse();
        editCategoryTitle.setText("category name");
        assertThat(saveButton.isEnabled()).isTrue();
        assertThat(cancelButton.getVisibility()).isEqualTo(View.VISIBLE);
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
        assertThat(cancelButton.getVisibility()).isEqualTo(View.INVISIBLE);
        editCategoryTitle.setText("category name");
        assertThat(cancelButton.getVisibility()).isEqualTo(View.VISIBLE);

        cancelButton.performClick();
        assertThat(cancelButton.getVisibility()).isEqualTo(View.INVISIBLE);
        assertThat(editCategoryTitle.getText().toString()).isEqualTo("");
        assertThat(categoryTitle.getText().toString()).isEqualTo("카테고리 이름");
    }

    @Test
    public void whenClickSaveButton_thenCallSaveNewCategoryItemAndReturnId() throws Exception {
        editCategoryTitle.setText("category name");
        when(repository.saveNewCategoryItemAndReturnId(any(CategoryModel.class))).thenReturn(1);
        saveButton.performClick();
        verify(repository).saveNewCategoryItemAndReturnId(any(CategoryModel.class));
    }

    @Test
    public void whenClickSaveButton_thenShowCardViewPagerWithAddCategoryItem() throws Exception {
        editCategoryTitle.setText("category name");
        when(repository.saveNewCategoryItemAndReturnId(any(CategoryModel.class))).thenReturn(1);
        saveButton.performClick();

        Intent actualIntent = shadowOf(subject).getNextStartedActivity();
        int actualColor = (int) actualIntent.getExtras().get(ApplicationConstants.CATEGORY_COLOR);

        assertThat(actualColor).isEqualTo(ResourceMapper.ColorType.RED.ordinal());
    }

    @Test
    public void whenSuccessSaveNewCategory_thenFinishActivity() throws Exception {
        editCategoryTitle.setText("category name");
        when(repository.saveNewCategoryItemAndReturnId(any(CategoryModel.class))).thenReturn(1);
        saveButton.performClick();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void whenLaunchedActivity_thenShowsIconListCorrectlyAndShowsDefaultPreview() throws Exception {
        RecyclerView iconList = subject.findViewById(R.id.icon_list);
        assertThat(iconList.getAdapter()).isNotNull();

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        NewCategoryItemAdapter adapter = (NewCategoryItemAdapter) iconList.getAdapter();
        assertThat(adapter.getItemCount()).isEqualTo(4);

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_hospital_select_dark);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_puzzle_used_dark);

        final ImageView previewIconImage = subject.findViewById(R.id.category_icon);
        assertThat(shadowOf(previewIconImage.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_hospital_menu);
    }

    @Test
    public void whenClickedIconList_thenChangedIconStateAndShowsInPreview() throws Exception {
        RecyclerView iconList = subject.findViewById(R.id.icon_list);

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        final int selectIndex = 2;
        iconList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_hospital_select_dark);

        final ImageView previewIconImage = subject.findViewById(R.id.category_icon);
        assertThat(shadowOf(previewIconImage.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_hospital_menu);
    }

    @Test
    public void whenClickedUsedIconInList_thenNothingToDo() throws Exception {
        RecyclerView iconList = subject.findViewById(R.id.icon_list);

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        final int selectIndex = 3;
        iconList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_hospital_select_dark);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) iconList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_puzzle_used_dark);
    }

    @Test
    public void whenLaunchedActivity_thenShowsColorListCorrectly() throws Exception {
        RecyclerView colorList = subject.findViewById(R.id.color_list);
        assertThat(colorList.getAdapter()).isNotNull();

        colorList.measure(0, 0);
        colorList.layout(0, 0, 10000, 100);

        NewCategoryItemAdapter adapter = (NewCategoryItemAdapter) colorList.getAdapter();
        assertThat(adapter.getItemCount()).isEqualTo(4);

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_color_red_select);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_color_yellow_used);

        final RelativeLayout previewColorLayout = subject.findViewById(R.id.new_category_color);
        assertThat(shadowOf(previewColorLayout.getBackground()).getCreatedFromResId())
                .isEqualTo(R.drawable.background_gradient_red);
    }

    @Test
    public void whenClickedColorList_thenChangedColorStateAndShowsInPreview() throws Exception {
        RecyclerView colorList = subject.findViewById(R.id.color_list);
        assertThat(colorList.getAdapter()).isNotNull();

        colorList.measure(0, 0);
        colorList.layout(0, 0, 10000, 100);

        final int selectIndex = 1;
        colorList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_color_red_select);

        final RelativeLayout previewColorLayout = subject.findViewById(R.id.new_category_color);
        assertThat(shadowOf(previewColorLayout.getBackground()).getCreatedFromResId())
                .isEqualTo(R.drawable.background_gradient_red);
    }

    @Test
    public void whenClickedUsedColorInList_thenNothingToDo() throws Exception {
        RecyclerView colorList = subject.findViewById(R.id.color_list);
        assertThat(colorList.getAdapter()).isNotNull();

        colorList.measure(0, 0);
        colorList.layout(0, 0, 10000, 100);

        final int selectIndex = 3;
        colorList.getChildAt(selectIndex).performClick();

        NewCategoryItemAdapter.NewCategoryItemViewHolder firstHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(0);
        assertThat(shadowOf(firstHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_color_red_select);

        NewCategoryItemAdapter.NewCategoryItemViewHolder lastHolder =
                (NewCategoryItemAdapter.NewCategoryItemViewHolder) colorList.findViewHolderForAdapterPosition(3);
        assertThat(shadowOf(lastHolder.categoryItem.getDrawable()).getCreatedFromResId())
                .isEqualTo(R.drawable.icon_color_yellow_used);
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
        RecyclerView iconList = subject.findViewById(R.id.icon_list);

        iconList.measure(0, 0);
        iconList.layout(0, 0, 10000, 100);

        editCategoryTitle.setText("test");
        final int selectIndex = 0;
        iconList.getChildAt(selectIndex).performClick();

        subject.onBackPressed();

        assertThat(ShadowAlertDialog.getLatestAlertDialog().isShowing()).isTrue();
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
