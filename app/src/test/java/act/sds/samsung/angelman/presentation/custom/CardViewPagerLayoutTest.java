package act.sds.samsung.angelman.presentation.custom;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.util.AngelManGlideTransform;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
    public void whenLaunchedCardViewPagerView_thenShowsCardListInSelectedCategory() throws Exception {
        when(repository.getSingleCardListWithCategoryId(anyInt())).thenReturn(getCardListWithCategoryId());
        subject.setCategoryData(setDefaultCategoryModel());

        CardViewPager viewPager = subject.cardViewPager;

        assertThat(viewPager.getAdapter()).isNotNull();
        assertThat(viewPager.getAdapter().getCount()).isNotEqualTo(0);

        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            viewPager.getAdapter().instantiateItem(viewPager, i);
        }

        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
        viewPager.requestLayout();

        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(0)).getCardTitleTextView().getText()).isEqualTo("물");


        ImageView cardImageView = ((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(0)).getCardImage();

        if (cardImageView.getDrawable() != null) {

            Bitmap actualImage = ((GlideBitmapDrawable) cardImageView.getDrawable()).getBitmap();

            try {
                ImageView expectedImageView = new ImageView(RuntimeEnvironment.application);
                expectedImageView.setLayoutParams(cardImageView.getLayoutParams());

                Glide.with(RuntimeEnvironment.application)
                        .load("file:///android_asset/water.png")
                        .bitmapTransform(new AngelManGlideTransform(RuntimeEnvironment.application, 10, 0, AngelManGlideTransform.CornerType.TOP))
                        .override(280, 280)
                        .into(expectedImageView);

                Bitmap expectedImage = ((GlideBitmapDrawable) expectedImageView.getDrawable()).getBitmap();
                assertThat(equals(actualImage, expectedImage)).isTrue();

            } catch (Exception ex) {
                ex.printStackTrace();
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
        when(repository.getSingleCardListWithCategoryId(anyInt())).thenReturn(getCardListWithCategoryId());
        subject.setCategoryData(setDefaultCategoryModel());

        assertThat(subject.getBackground()).isEqualTo(getDrawable(R.drawable.background_gradient_red));

    }

    private CategoryModel setDefaultCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.title = "먹을 것";
        categoryModel.color = ResourcesUtil.RED;
        categoryModel.index = 0;

        return categoryModel;
    }

    private ArrayList<CardModel> getCardListWithCategoryId() {
        ArrayList<CardModel> ret = new ArrayList<>();
        addSingleCardModel(ret, "물", "water.png", "20010928_120020");
        addSingleCardModel(ret, "유유", "milk.png", "20010928_120019");
        addSingleCardModel(ret, "쥬스", "juice.png", "20010928_120015");
        return ret;
    }

    private void addSingleCardModel(ArrayList list, String name, String path, String time) {
        CardModel model = new CardModel(name, path, time);
        list.add(model);
    }

    private boolean equals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }
}