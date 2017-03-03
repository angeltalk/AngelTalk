package act.sds.samsung.angelman;


import act.sds.samsung.angelman.dagger.components.AngelmanTestComponent;
import act.sds.samsung.angelman.dagger.components.DaggerAngelmanTestComponent;
import act.sds.samsung.angelman.dagger.modules.AngelmanTestModule;

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
