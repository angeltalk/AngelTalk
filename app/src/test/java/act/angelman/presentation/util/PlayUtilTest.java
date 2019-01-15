package act.angelman.presentation.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class PlayUtilTest {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void givenTTSText_whenCallSpeakMethodInTTSUtilClass_thenMustCalled() {
        String txt = "Test";
        TextToSpeech tts = mock(TextToSpeech.class);

        PlayUtil util = PlayUtil.getInstance();
        util.tts = tts;
        util.ttsSpeak(txt);

        verify(tts).speak(eq(txt), eq(TextToSpeech.QUEUE_FLUSH), any(Bundle.class), anyString());
    }
}