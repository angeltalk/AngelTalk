package act.angelman;


import act.angelman.dagger.components.AngelmanTestComponent;
import act.angelman.dagger.components.DaggerAngelmanTestComponent;
import act.angelman.dagger.modules.AngelmanTestModule;

public class TestAngelmanApplication extends AngelmanApplication {

    private AngelmanTestComponent angelmanComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        angelmanComponent = DaggerAngelmanTestComponent.builder()
                .angelmanModule(new AngelmanTestModule(this))
                .build();

        setComponent(angelmanComponent);
    }

    public AngelmanTestComponent getAngelmanTestComponent() {
        return this.angelmanComponent;
    }
}
