package angeltalk.plus.dagger.modules;

import android.content.Context;

import javax.inject.Singleton;

import angeltalk.plus.data.repository.CardDataRepository;
import angeltalk.plus.data.repository.CategoryDataRepository;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.domain.repository.CategoryRepository;
import angeltalk.plus.presentation.manager.ApplicationManager;
import angeltalk.plus.presentation.manager.NotificationActionManager;
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
    NotificationActionManager providesNotificationActionManager() {
        return  new NotificationActionManager(context.getApplicationContext());
    }
}
