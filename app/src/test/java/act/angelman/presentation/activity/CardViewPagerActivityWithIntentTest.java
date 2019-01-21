package act.angelman.presentation.activity;

import android.content.Intent;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.network.transfer.CardTransfer;
import act.angelman.network.transfer.MessageTransfer;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.shadow.ShadowSnackbar;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.ResourcesUtil;
import androidx.test.core.app.ApplicationProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowSnackbar.class})
public class CardViewPagerActivityWithIntentTest extends UITest{

    @Inject
    CardRepository repository;

    @Inject
    CardTransfer cardTransfer;

    @Inject
    MessageTransfer messageTransfer;

    @Inject
    ApplicationManager applicationManager;

    private CardViewPagerActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) ApplicationProvider.getApplicationContext() ).getAngelmanTestComponent().inject(this);
        when(cardTransfer.isConnectedToNetwork()).thenReturn(true);
        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        when(applicationManager.getCategoryModelColor()).thenReturn(getCategoryModelColor());
    }

    @Test
    public void whenCardEditSuccess_thenShowTheCard() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CardViewPagerActivity.class);
        intent.putExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, true);

        when(applicationManager.getCurrentCardIndex()).thenReturn(1);
        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(2);
    }

    @Test
    public void whenCardEditSuccess_thenShowSnackBarMessage() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CardViewPagerActivity.class);
        intent.putExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, true);

        setupActivityWithIntent(CardViewPagerActivity.class, intent);

        subject = setupActivity(CardViewPagerActivity.class);

        assertThat(ShadowSnackbar.getLatestSnackbar()).isNotNull();
        assertThat(ShadowSnackbar.getTextOfLatestSnackbar()).isEqualTo("카드가 수정되었습니다");
    }


    @Test
    public void whenFinishedToMakeNewCard_thenShowsNewAddedCardAtFirstCardInViewPager() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CardViewPagerActivity.class);
        intent.putExtra(ApplicationConstants.INTENT_KEY_NEW_CARD, true);

        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isNotEqualTo(0);
        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(1);
    }


    private List<CardModel> getCardListWithCategoryId() {

        String contentFolder = ContentsUtil.getContentFolder(ApplicationProvider.getApplicationContext()) + File.separator;

        List<CardModel> ret = Lists.newArrayList(
                makeSingleCardModel("1", "물", contentFolder+"water.jpg", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel("2",  "우유", contentFolder+"milk.png", "20010928_120019", 0, 1, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel("3",  "쥬스", contentFolder+"juice.png", "20010928_120015", 0, 2, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel("4",  "젤리", contentFolder+"haribo.mp4", "20010928_120015", 0, 3, CardModel.CardType.VIDEO_CARD, contentFolder+"haribo.jpg", false)
        );

        return ret;
    }

    private CardModel makeSingleCardModel(String id, String name, String path, String time, int categoryId, int cardIndex, CardModel.CardType cardType, String thumbnailPath , boolean hide) {
        return CardModel.builder()._id(id).name(name).contentPath(path).firstTime(time).categoryId(categoryId).cardIndex(cardIndex).cardType(cardType).thumbnailPath(thumbnailPath).hide(hide).build();
    }

    private CategoryModel getCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.index = 0;
        categoryModel.title = "먹을 것";
        categoryModel.color = ResourcesUtil.RED;
        return categoryModel;
    }

    private int getCategoryModelColor() {
        return ResourcesUtil.getCardViewLayoutBackgroundBy(getCategoryModel().color);
    }

}
