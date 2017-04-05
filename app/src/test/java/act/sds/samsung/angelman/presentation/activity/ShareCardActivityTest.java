package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CardTransferModel;
import act.sds.samsung.angelman.presentation.listener.OnDownloadCompleteListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShareCardActivityTest extends UITest {

    ShareCardActivity subject;


    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);


    }

    @Test
    public void whenKaKaoIntentReceived_thenShowLoadingAnimation () throws Exception{
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme)+"://"+ getString(R.string.kakaolink_host)));
        subject = setupActivityWithIntent(ShareCardActivity.class, intent);
        LinearLayout loadingViewLayout  = (LinearLayout) subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }


    @Test
    public void whenDownloadCardComplete_thenHideLoadingAnimationAndShowShareCard () throws Exception{
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme)+"://"+ getString(R.string.kakaolink_host)));
        subject = setupActivityWithIntent(ShareCardActivity.class, intent);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                OnDownloadCompleteListener onSuccessListener = ((OnDownloadCompleteListener) invocation.getArguments()[1]);
                CardTransferModel cardTransferModel = new CardTransferModel();
                cardTransferModel.name="TESTCARD";
                cardTransferModel.cardType = CardModel.CardType.PHOTO_CARD.getValue();
                cardTransferModel.contentPath="/content";
                String filePath = "/";
                onSuccessListener.onSuccess(cardTransferModel,filePath);
                return  null;

            }
        }).when(subject.cardTransfer).downloadCard(any(String.class), any(OnDownloadCompleteListener.class));

        LinearLayout loadingViewLayout  = (LinearLayout) subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);
        subject.downloadCard();
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.GONE);
        TextView shareCardTextView = (TextView) subject.mViewPager.getChildAt(subject.mViewPager.getCurrentItem()).findViewById(R.id.card_image_title);
        assertThat(shareCardTextView.getText()).isEqualTo("TESTCARD");
    }

}