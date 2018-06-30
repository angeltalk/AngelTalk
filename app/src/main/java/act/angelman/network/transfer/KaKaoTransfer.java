package act.angelman.network.transfer;


import android.content.Context;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.kakaolink.v2.model.ButtonObject;
import com.kakao.kakaolink.v2.model.ContentObject;
import com.kakao.kakaolink.v2.model.FeedTemplate;
import com.kakao.kakaolink.v2.model.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.presentation.manager.ApplicationManager;

public class KaKaoTransfer {

    @Inject
    ApplicationManager applicationManager;

    private Context context;

    public KaKaoTransfer(Context context){
        this.context = context;
        ((AngelmanApplication) context).getAngelmanComponent().inject(this);
    }

    public void sendKakaoLinkMessage(Context activityContext, String key, String thumbnailImageUrl, CardModel card) {
        makeTemplate(activityContext, key, thumbnailImageUrl, card);
    }

    private void makeTemplate(Context activityContext, String key, String thumbnailImageUrl, CardModel card) {
        String kakaoNewCardMessage = activityContext.getString(R.string.kakao_add_new_card_text, card.name);

        FeedTemplate params = getFeedTemplate(activityContext, key, thumbnailImageUrl, kakaoNewCardMessage);

        sendKakaoLink(activityContext, params);
    }

    private FeedTemplate getFeedTemplate(Context activityContext, String key, String thumbnailImageUrl, String kakaoNewCardMessage) {
        return FeedTemplate
                    .newBuilder(ContentObject.newBuilder(kakaoNewCardMessage,
                            thumbnailImageUrl,
                            LinkObject.newBuilder().setWebUrl(activityContext.getString(R.string.web_url))
                                    .setMobileWebUrl(activityContext.getString(R.string.web_url)).build())
                            .setDescrption(kakaoNewCardMessage)
                            .build())
                    .addButton(new ButtonObject(activityContext.getString(R.string.kakao_button_go_to_app), LinkObject.newBuilder()
                            .setWebUrl(activityContext.getString(R.string.web_url))
                            .setMobileWebUrl(activityContext.getString(R.string.web_url))
                            .setAndroidExecutionParams("key="+key).build()))
                    .build();
    }

    private void sendKakaoLink(Context activityContext, FeedTemplate params) {
        KakaoLinkService.getInstance().sendDefault(activityContext, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Toast.makeText(context, errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {}
        });
    }
}
