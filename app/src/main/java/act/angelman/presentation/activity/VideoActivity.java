package act.angelman.presentation.activity;

import android.app.Activity;
import android.os.Bundle;

import act.angelman.R;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.fragment.VideoFragment;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, VideoFragment.newInstance(this), ApplicationConstants.VIDEO_FRAGMENT_TAG)
                    .commit();
        }
    }

}
