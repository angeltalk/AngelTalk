package angeltalk.plus.presentation.custom;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import angeltalk.plus.R;
import angeltalk.plus.TestAngelmanApplication;
import angeltalk.plus.UITest;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.presentation.adapter.CardImageAdapter;
import angeltalk.plus.presentation.util.AngelManGlideTransform;
import angeltalk.plus.presentation.util.PlayUtil;
import angeltalk.plus.presentation.util.ResourcesUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class CardViewPagerLayoutTest extends UITest{

    @Inject
    CardRepository repository;

    private CardViewPagerLayout subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        subject = new CardViewPagerLayout(RuntimeEnvironment.application, null);
    }

    @Test
    public void whenLaunchedCardViewPagerView_thenShowsCategoryNameOnTitleCorrectly() throws Exception {
        subject.setCategoryData(setDefaultCategoryModel());
        TextView title = (TextView) subject.findViewById(R.id.category_item_title);
        assertThat(title.getText()).isEqualTo("먹을 것");
    }

    @Test
    public void whenLaunchedCardViewPager_thenShowOnlyOneCardWithoutSideHintCard() throws Exception {
        subject.setCategoryData(setDefaultCategoryModel());
        assertThat(subject.getChildCount() == 1).isTrue();
    }

    @Test
    public void whenLaunchedCardViewPagerView_thenShowOXButton() throws Exception {
        subject.setCategoryData(setDefaultCategoryModel());
        ImageView yesNoButton = (ImageView) subject.findViewById(R.id.yes_no_btn);
        assertThat(yesNoButton.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void whenClickYesNoButton_thenShowYesNoLayout() throws Exception {
        subject.setCategoryData(setDefaultCategoryModel());
        ImageView yesNoButton = (ImageView) subject.findViewById(R.id.yes_no_btn);
        ConstraintLayout backgorundLayout  = (ConstraintLayout) subject.findViewById(R.id.yes_no_pannel);
        assertThat(backgorundLayout.getVisibility()).isEqualTo(View.GONE);
        yesNoButton.performClick();
        assertThat(backgorundLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }


    @Test
    public void givenShowYesNoLayout_whenClickYesNoCloseButton_thenHideYesNoLayout() throws Exception {
        subject.setCategoryData(setDefaultCategoryModel());
        ImageView yesNoButton = (ImageView) subject.findViewById(R.id.yes_no_btn);
        ConstraintLayout backgorundLayout  = (ConstraintLayout) subject.findViewById(R.id.yes_no_pannel);
        yesNoButton.performClick();
        assertThat(backgorundLayout.getVisibility()).isEqualTo(View.VISIBLE);

        ImageView yesNoCloseButton = (ImageView) subject.findViewById(R.id.yes_no_close_btn);
        yesNoCloseButton.performClick();

        assertThat(backgorundLayout.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void whenClickYesNoButton_thenSlideUpAnimation() throws Exception {
        subject.setCategoryData(setDefaultCategoryModel());
        ImageView yesNoButton = (ImageView) subject.findViewById(R.id.yes_no_btn);
        ConstraintLayout mockLayout = mock(ConstraintLayout.class);
        subject.yesNoPannel = mockLayout;

        yesNoButton.performClick();

        verify(subject.yesNoPannel).startAnimation(any(Animation.class));
    }

    @Test
    public void givenShowYesNoLayout_whenClickYes_thenTtsSpeak() throws Exception {
        subject.setCategoryData(setDefaultCategoryModel());
        ImageView yesNoButton = (ImageView) subject.findViewById(R.id.yes_no_btn);
        yesNoButton.performClick();

        PlayUtil ttsMock = mock(PlayUtil.class);

        Field declaredField = CardViewPagerLayout.class.getDeclaredField("playUtil");
        declaredField.setAccessible(true);
        declaredField.set(subject, ttsMock);

        doNothing().when(ttsMock).ttsSpeak("네");
        LinearLayout yesLayout = (LinearLayout) subject.findViewById(R.id.yes_layout);
        yesLayout.performClick();
        verify(ttsMock).ttsSpeak("네");
    }



    @Test
    public void whenLaunchedCardViewPagerView_thenShowsCardListInSelectedCategory() throws Exception {
        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        subject.setCategoryData(setDefaultCategoryModel());

        CardViewPager viewPager = initViewPager();

        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(0)).cardTitle.getText()).isEqualTo("버스");

        if (viewPager != null) {
            ImageView cardImageView = ((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(0)).cardImage;

            if (cardImageView.getDrawable() != null) {

                Bitmap actualImage = ((BitmapDrawable) cardImageView.getDrawable()).getBitmap();

                try {
                    ImageView expectedImageView = new ImageView(RuntimeEnvironment.application);
                    expectedImageView.setLayoutParams(cardImageView.getLayoutParams());

                    Glide.with(RuntimeEnvironment.application)
                            .load("file:///android_asset/bus.png")
                            .bitmapTransform(new AngelManGlideTransform(RuntimeEnvironment.application, 10, 0, AngelManGlideTransform.CornerType.TOP))
                            .into(expectedImageView);

                    Bitmap expectedImage = ((BitmapDrawable) expectedImageView.getDrawable()).getBitmap();
                    assertThat(equals(actualImage, expectedImage)).isTrue();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    @Test
    public void whenClickedBackButton_thenGoBackToCategoryLockScreenView() throws Exception {
        CardViewPagerLayout.OnClickBackButtonListener mock = mock(CardViewPagerLayout.OnClickBackButtonListener.class);
        subject.setOnClickBackButtonListener(mock);
        subject.findViewById(R.id.back_button).performClick();

        verify(mock).clickBackButton();

    }

    @Test
    public void whenClickCategoryMenu_ShowCardViewPagerLayoutAndSetBackgroudColor() throws Exception {
        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        subject.setCategoryData(setDefaultCategoryModel());

        assertThat(shadowOf(subject.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_yellow);
    }

    @Test
    public void whenPageChanged_thenViewChange() throws Exception {
        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        subject.setCategoryData(setDefaultCategoryModel());

        CardViewPager viewPager = initViewPager();

        viewPager.setCurrentItem(1);
        assertThat(subject.currentCardIndex).isEqualTo(1);
    }

    @NonNull
    private CardViewPager initViewPager() {
        CardViewPager viewPager = subject.mViewPager;
        assertThat(viewPager.getAdapter()).isNotNull();
        assertThat(viewPager.getAdapter().getCount()).isNotEqualTo(0);

        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            viewPager.getAdapter().instantiateItem(viewPager, i);
        }

        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
        viewPager.requestLayout();
        return viewPager;
    }

    private CategoryModel setDefaultCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.title = "먹을 것";
        categoryModel.color = ResourcesUtil.YELLOW;
        categoryModel.index = 0;

        return categoryModel;
    }

    private List<CardModel> getCardListWithCategoryId() {
        List<CardModel> ret = Lists.newArrayList(
                makeSingleCardModel("버스", "bus.png", "20010928_120020"),
                makeSingleCardModel("유유", "milk.png", "20010928_120019"),
                makeSingleCardModel("쥬스", "juice.png", "20010928_120015")
        );

        return ret;
    }

    private CardModel makeSingleCardModel(String name, String path, String time) {
        return CardModel.builder().name(name).contentPath(path).firstTime(time).hide(false).build();
    }

    private boolean equals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }
}