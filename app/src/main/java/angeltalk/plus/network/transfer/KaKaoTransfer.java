package angeltalk.plus.network.transfer;


import android.content.Context;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.presentation.manager.ApplicationManager;
import dagger.hilt.android.qualifiers.ApplicationContext;

// TODO(phase-8): Rewrite on top of Kakao SDK v2 (com.kakao.sdk.share.ShareClient).
// The legacy v1 KakaoLink API (com.kakao.kakaolink.v2.*) is unavailable because the
// devrepo.kakao.com Maven server is dead. All methods are no-ops until the rewrite.
@Singleton
public class KaKaoTransfer {

    private static final String TAG = "KaKaoTransfer";

    private final Context context;
    private final ApplicationManager applicationManager;

    @Inject
    public KaKaoTransfer(@ApplicationContext Context context, ApplicationManager applicationManager) {
        this.context = context;
        this.applicationManager = applicationManager;
    }

    public void sendKakaoLinkMessage(Context activityContext, String key, String thumbnailImageUrl, CardModel card) {
        Log.w(TAG, "sendKakaoLinkMessage is stubbed — Kakao SDK v2 migration pending (phase 8).");
    }
}
