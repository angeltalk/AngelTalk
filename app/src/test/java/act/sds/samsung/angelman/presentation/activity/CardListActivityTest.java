package act.sds.samsung.angelman.presentation.activity;

import android.widget.ImageView;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.custom.FontTextView;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CardListActivityTest extends UITest{

    @Inject
    ApplicationManager applicationManager;

    @Inject
    CardRepository cardRepository;

    private CardListActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        when(cardRepository.getSingleCardListWithCategoryId(applicationManager.getCategoryModel().index)).thenReturn(getCardModelList());
        subject = setupActivity(CardListActivity.class);
    }

    @Test
    public void whenLaunched_thenShowTitleCorrectly() throws Exception {
        assertThat(shadowOf(subject.titleLayout.getBackground()).getCreatedFromResId()).isEqualTo(R.color.simple_background_red);
        assertThat(subject.categoryItemTitle.getText()).isEqualTo("먹을 것");
    }

    @Test
    public void whenLaunched_thenShowCardListInCategory() throws Exception {
        assertThat(subject.cardListRecyclerView).isNotNull();
        assertThat(subject.cardListRecyclerView.getChildCount()).isEqualTo(8);
    }

    @Test
    public void givenShowingCardInList_whenLaunched_thenShowIcon() throws Exception {
        ImageView showHideBarView = ((ImageView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.show_hide_item_bar));
        ImageView showHideIconView = ((ImageView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.show_hide_icon));
        FontTextView cardName = ((FontTextView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.card_name));
        ImageView cardThumbnail = ((ImageView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.card_thumbnail));

        assertThat(shadowOf(showHideBarView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.show_red);
        assertThat(shadowOf(showHideIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.ic_show_red);
        assertThat(cardName.getCurrentTextColor()).isEqualTo(subject.getResources().getColor(R.color.black_00));
        assertThat(cardThumbnail.getImageAlpha()).isEqualTo(255);
    }

    @Test
    public void givenHidingCardInList_whenLaunched_thenHideIcon() throws Exception {
        ImageView showHideBarView = ((ImageView) subject.cardListRecyclerView.getChildAt(2).findViewById(R.id.show_hide_item_bar));
        ImageView showHideIconView = ((ImageView) subject.cardListRecyclerView.getChildAt(2).findViewById(R.id.show_hide_icon));
        FontTextView cardName = ((FontTextView) subject.cardListRecyclerView.getChildAt(2).findViewById(R.id.card_name));
        ImageView cardThumbnail = ((ImageView) subject.cardListRecyclerView.getChildAt(2).findViewById(R.id.card_thumbnail));

        assertThat(shadowOf(showHideBarView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.hide);
        assertThat(shadowOf(showHideIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.ic_hide);
        assertThat(cardName.getCurrentTextColor()).isEqualTo(subject.getResources().getColor(R.color.black_4C));
        assertThat(cardThumbnail.getImageAlpha()).isEqualTo(60);
    }

    @Test
    public void whenClickRecyclerViewItem_thenShowHideChangeAndDatabaseUpdate() throws Exception {

        ImageView showHideBarView = ((ImageView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.show_hide_item_bar));
        assertThat(shadowOf(showHideBarView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.show_red);

        // when
        subject.cardListRecyclerView.getChildAt(0).performClick();

        // then
        showHideBarView = ((ImageView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.show_hide_item_bar));
        ImageView showHideIconView = ((ImageView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.show_hide_icon));
        FontTextView cardName = ((FontTextView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.card_name));
        ImageView cardThumbnail = ((ImageView) subject.cardListRecyclerView.getChildAt(0).findViewById(R.id.card_thumbnail));

        assertThat(shadowOf(showHideBarView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.hide);
        assertThat(shadowOf(showHideIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.ic_hide);
        assertThat(cardName.getCurrentTextColor()).isEqualTo(subject.getResources().getColor(R.color.black_4C));
        assertThat(cardThumbnail.getImageAlpha()).isEqualTo(60);

        verify(cardRepository).updateSingleCardModelHide(any(CardModel.class));
    }


    @Test
    public void whenClickBackButton_thenFinishActivity() throws Exception {
        subject.findViewById(R.id.back_button).performClick();
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void whenClickAddCardButton_thenMoveToCameraGallerySelectionActivity() throws Exception {
        // when
        subject.findViewById(R.id.add_card_button).performClick();

        // then
        ShadowActivity shadowActivity = shadowOf(subject);
        assertThat(shadowActivity.getNextStartedActivity().getComponent().getClassName()).isEqualTo(CameraGallerySelectionActivity.class.getCanonicalName());
    }

    private CategoryModel getCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.index = 0;
        categoryModel.title = "먹을 것";
        categoryModel.color = ResourcesUtil.RED;
        return categoryModel;
    }

    private List<CardModel> getCardModelList() {
        List<CardModel> list = Lists.newArrayList();

        String contentFolder = ContentsUtil.getContentFolder() + File.separator;

        list.add(CardModel.builder().name("물0").contentPath(contentFolder+"water.png").firstTime("20161018_000003").categoryId(0).cardIndex(0).cardType(CardModel.CardType.PHOTO_CARD).thumbnailPath("").hide(false).build());
        list.add(CardModel.builder().name("물1").contentPath(contentFolder+"water.png").firstTime("20161018_000003").categoryId(0).cardIndex(1).cardType(CardModel.CardType.PHOTO_CARD).thumbnailPath("").hide(true).build());
        list.add(CardModel.builder().name("물2").contentPath(contentFolder+"water.png").firstTime("20161018_000003").categoryId(0).cardIndex(2).cardType(CardModel.CardType.PHOTO_CARD).thumbnailPath("").hide(true).build());
        list.add(CardModel.builder().name("물3").contentPath(contentFolder+"water.png").firstTime("20161018_000003").categoryId(0).cardIndex(3).cardType(CardModel.CardType.PHOTO_CARD).thumbnailPath("").hide(false).build());
        list.add(CardModel.builder().name("물4").contentPath(contentFolder+"water.png").firstTime("20161018_000003").categoryId(0).cardIndex(4).cardType(CardModel.CardType.PHOTO_CARD).thumbnailPath("").hide(false).build());
        list.add(CardModel.builder().name("물5").contentPath(contentFolder+"water.png").firstTime("20161018_000003").categoryId(0).cardIndex(5).cardType(CardModel.CardType.PHOTO_CARD).thumbnailPath("").hide(false).build());
        list.add(CardModel.builder().name("젤리0").contentPath(contentFolder+"haribo.mp4").firstTime("20161018_000003").categoryId(0).cardIndex(6).cardType(CardModel.CardType.VIDEO_CARD).thumbnailPath(contentFolder+"haribo.jpg").hide(false).build());
        list.add(CardModel.builder().name("젤리1").contentPath(contentFolder+"haribo.mp4").firstTime("20161018_000003").categoryId(0).cardIndex(7).cardType(CardModel.CardType.VIDEO_CARD).thumbnailPath(contentFolder+"haribo.jpg").hide(true).build());

        return list;
    }



}