package angeltalk.plus.dagger.modules;

import android.content.Context;

import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.domain.repository.CategoryRepository;
import angeltalk.plus.network.transfer.CardTransfer;
import angeltalk.plus.network.transfer.KaKaoTransfer;
import angeltalk.plus.network.transfer.MessageTransfer;
import angeltalk.plus.presentation.manager.ApplicationManager;
import angeltalk.plus.presentation.manager.NotificationActionManager;

import static org.mockito.Mockito.mock;

public class AngelmanTestModule extends angeltalk.plus.dagger.modules.AngelmanModule {

    public AngelmanTestModule(Context context) {
        super(context);
    }

    @Override
    CardRepository providesSingleCardRepository() {
        return mock(CardRepository.class);
    }

    @Override
    CategoryRepository providesCategoryRepository() {
        return mock(CategoryRepository.class);
    }

    @Override
    ApplicationManager providesApplicationManager() {
        return mock(ApplicationManager.class);
    }

    @Override
    CardTransfer providesCardTransfer()  {
        return mock(CardTransfer.class);
    }

    @Override
    KaKaoTransfer providesKaKaoTransfer()  {
        return mock(KaKaoTransfer.class);
    }

    @Override
    MessageTransfer providesMessageTransfer()  {
        return mock(MessageTransfer.class);
    }

    @Override
    NotificationActionManager providesNotificationActionManager() {
        return mock(NotificationActionManager.class);
    }
}
