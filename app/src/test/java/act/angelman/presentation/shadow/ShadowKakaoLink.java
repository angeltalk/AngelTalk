package act.angelman.presentation.shadow;

import android.content.Context;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.util.KakaoParameterException;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import static org.mockito.Mockito.mock;

@Implements(KakaoLink.class)
public class ShadowKakaoLink {

    @Implementation
    public static KakaoLink getKakaoLink(final Context context) throws KakaoParameterException {
        return mock(KakaoLink.class);
    }
}
