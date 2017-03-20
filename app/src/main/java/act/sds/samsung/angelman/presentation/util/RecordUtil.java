package act.sds.samsung.angelman.presentation.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;

public class RecordUtil {

    private static RecordUtil instance = null;

    private RecordUtil() {}

    public static RecordUtil getInstance() {
        if (instance == null)
            instance = new RecordUtil();

        return instance;
    }

    public interface RecordCallback {
        void afterRecord();
    }

    private static final int STATE_NONE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_RECORDING = 2;

    private int state = STATE_NONE;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;

    public static String getMediaFilePath(){
        return FileUtil.getVoiceFolder() + File.separator + DateUtil.getDateNow() +".3gdp";
    }

    public void stopRecord() {
        if (state == STATE_RECORDING) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;

            state = STATE_NONE;
        }
    }

    public void record(String mediaFile, final RecordCallback callback) {
        if (state == STATE_NONE) {

            File outFile = new File(mediaFile);

            if (outFile.exists()) {
                outFile.delete();
            }
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(mediaFile);

            try {
                state = STATE_RECORDING;
                mediaRecorder.prepare();
                mediaRecorder.start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (state == STATE_RECORDING) {
                            stopRecord();
                            state = STATE_NONE;
                            callback.afterRecord();
                        }
                    }
                }, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
