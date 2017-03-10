package act.sds.samsung.angelman.dagger.modules;

import android.content.Context;

import javax.inject.Singleton;

import act.sds.samsung.angelman.data.firebase.FirebaseSynchronizer;
import act.sds.samsung.angelman.data.repository.CardDataRepository;
import act.sds.samsung.angelman.data.repository.CategoryDataRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class AngelmanModule {

    Context context;
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
    FirebaseSynchronizer providesFirebaseSynchronizer() {
        return new FirebaseSynchronizer(context.getApplicationContext());
    }


}
