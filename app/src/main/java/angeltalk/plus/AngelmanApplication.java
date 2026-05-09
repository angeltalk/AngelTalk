package angeltalk.plus;


import android.app.Application;
import androidx.annotation.VisibleForTesting;

import angeltalk.plus.dagger.components.AngelmanComponent;
import angeltalk.plus.dagger.components.DaggerAngelmanComponent;
import angeltalk.plus.dagger.modules.AngelmanModule;
import angeltalk.plus.presentation.manager.ApplicationInitializer;

public class AngelmanApplication extends Application {

    private AngelmanComponent angelmanComponent;
    private ApplicationInitializer applicationInitializer;

    @Override
    public void onCreate() {
        super.onCreate();

        angelmanComponent = DaggerAngelmanComponent.builder()
                .angelmanModule(new AngelmanModule(this))
                .build();

        applicationInitializer = new ApplicationInitializer(getApplicationContext());
        applicationInitializer.initializeApplication();
    }

    public AngelmanComponent getAngelmanComponent() {
        return this.angelmanComponent;
    }

    @VisibleForTesting
    public void setComponent(AngelmanComponent angelmanComponent) {
        this.angelmanComponent = angelmanComponent;
    }


}
