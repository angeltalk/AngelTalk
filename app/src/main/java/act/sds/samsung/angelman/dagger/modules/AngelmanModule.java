package act.sds.samsung.angelman.dagger.modules;

import android.content.Context;

import javax.inject.Singleton;

import act.sds.samsung.angelman.data.repository.CardDataRepository;
import act.sds.samsung.angelman.data.repository.CategoryDataRepository;
import act.sds.samsung.angelman.network.transfer.CardTransfer;
import act.sds.samsung.angelman.network.transfer.KaKaoTransfer;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import dagger.Module;
import dagger.Provides;

@Module
public class AngelmanModule {

    private Context context;

    public AngelmanModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    CardRepository providesSingleCardRepository() {
        return new CardDataRepository(context.getApplicationContext());
    }

    @Provides
    @Singleton
    CategoryRepository providesCategoryRepository() {
        return new CategoryDataRepository(context.getApplicationContext());
    }

    @Provides
    @Singleton
    ApplicationManager providesApplicationManager() {
        return  new ApplicationManager(context.getApplicationContext());
    }


    @Provides
    @Singleton
    CardTransfer providesCardTransfer() {
        return  new CardTransfer(context.getApplicationContext());
    }

    @Provides
    @Singleton
    KaKaoTransfer providesKaKaoTransfer() {
        return  new KaKaoTransfer(context.getApplicationContext());
    }
}
