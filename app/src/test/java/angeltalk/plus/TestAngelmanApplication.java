package angeltalk.plus;


import angeltalk.plus.dagger.components.AngelmanTestComponent;
import angeltalk.plus.dagger.components.DaggerAngelmanTestComponent;
import angeltalk.plus.dagger.modules.AngelmanTestModule;

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
