package act.angelman.presentation.activity;

import android.os.Bundle;

import act.angelman.R;
import act.angelman.presentation.fragment.VideoFragment;
import act.angelman.presentation.manager.ApplicationConstants;

public class VideoActivity extends AbstractActivity {

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
