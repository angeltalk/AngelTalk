package angeltalk.plus.presentation.activity;

import android.os.Bundle;

import angeltalk.plus.R;
import angeltalk.plus.presentation.fragment.VideoFragment;
import angeltalk.plus.presentation.manager.ApplicationConstants;

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
