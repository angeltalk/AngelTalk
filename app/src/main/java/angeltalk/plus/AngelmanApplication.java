package angeltalk.plus;


import android.app.Application;

import javax.inject.Inject;

import angeltalk.plus.presentation.manager.ApplicationInitializer;
import dagger.hilt.android.HiltAndroidApp;

// TODO(phase-9): restore ACRA 5 crash reporting. The ACRA builder API changed in 5.x and
// the annotation-based path needs `ch.acra:annotationprocessor` wired. Removed for now so
// the Hilt/AGP 8 migration can build. Manifest `kakao_app_key` meta-data and deps
// (acra-mail, acra-dialog) remain in place for easy re-enable.
@HiltAndroidApp
public class AngelmanApplication extends Application {

    @Inject
    ApplicationInitializer applicationInitializer;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInitializer.initializeApplication();
    }
}
