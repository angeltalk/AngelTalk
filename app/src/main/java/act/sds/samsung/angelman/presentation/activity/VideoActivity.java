package act.sds.samsung.angelman.presentation.activity;

import android.app.Activity;
import android.os.Bundle;

import act.sds.samsung.angelman.R;

public class VideoActivity extends Activity {

    public static final String VIDEO_FRAGMENT_TAG = "VIDEO_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, VideoFragment.newInstance(this), VIDEO_FRAGMENT_TAG)
                    .commit();
        }
    }

}
