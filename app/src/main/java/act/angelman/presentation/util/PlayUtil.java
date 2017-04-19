package act.angelman.presentation.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.IOException;

public class PlayUtil implements TextToSpeech.OnInitListener {

    private static final int STATE_NONE = 0;
    private static final int STATE_PLAYING = 1;

    TextToSpeech tts;
    private int state;
    private MediaPlayer mediaPlayer;

    private static PlayUtil instance = null;

    private PlayUtil() {}

    public static PlayUtil getInstance() {
        if (instance == null)
            instance = new PlayUtil();

        return instance;
    }

    @Override
    public void onInit(int status) {
        if(tts != null && status == TextToSpeech.SUCCESS && state == STATE_NONE){
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    state = STATE_PLAYING;
                }

                @Override
                public void onDone(String utteranceId) {
                    ttsStop();
                }

                @Override
                public void onError(String utteranceId) {

                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void ttsSpeak(final String text) {
        if(tts != null  && state == STATE_NONE) {
            Bundle bundle = new Bundle();
            bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text);
        }
    }

    public void ttsStop() {
        if(tts == null) return;

        if(state == STATE_PLAYING) {
            tts.stop();

            state = STATE_NONE;
        }
    }

    public void playStop() {
        if(mediaPlayer == null) return;
        if(state == STATE_PLAYING){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            state = STATE_NONE;
        }
    }

    public void play(String mediaFile) {
        if (state == STATE_NONE) {
            try {
                state = STATE_PLAYING;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(mediaFile);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playStop();
                    }
                }, 3000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initTts(Context context) {
        if(tts == null) {
            tts = new TextToSpeech(context, this);
        }
    }
}

