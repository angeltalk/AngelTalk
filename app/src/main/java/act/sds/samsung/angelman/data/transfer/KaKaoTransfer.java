package act.sds.samsung.angelman.data.transfer;


import android.content.Context;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;

public class KaKaoTransfer {

    @Inject
    ApplicationManager applicationManager;

    Context context;

    public KaKaoTransfer(Context context){
        this.context = context;
        ((AngelmanApplication) context).getAngelmanComponent().inject(this);
    }

    public void sendKakaoLinkMessage(Context activityContext, String key, String thumbnailImageUrl, CardModel card) {
        try {
            KakaoLink kakaoLink = applicationManager.getKakaoLink();
            KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            String kakaoNewCardMessage = activityContext.getString(R.string.kakao_add_new_card_text, card.name);
            kakaoTalkLinkMessageBuilder.addText(kakaoNewCardMessage);
            kakaoTalkLinkMessageBuilder.addImage(thumbnailImageUrl, 300, 300);
            kakaoTalkLinkMessageBuilder.addAppButton(activityContext.getString(R.string.kakao_button_go_to_app), new AppActionBuilder().addActionInfo(
                    AppActionInfoBuilder.createAndroidActionInfoBuilder()
                            .setExecuteParam("key="+key)
                            .setMarketParam("referrer=kakakotalklink")
                            .build())
                    .setUrl(activityContext.getString(R.string.web_url))
                    .build());

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, activityContext);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }
}
