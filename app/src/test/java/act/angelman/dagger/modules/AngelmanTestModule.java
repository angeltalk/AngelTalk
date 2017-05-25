package act.angelman.dagger.modules;

import android.content.Context;

import act.angelman.domain.repository.CardRepository;
import act.angelman.domain.repository.CategoryRepository;
import act.angelman.network.transfer.CardTransfer;
import act.angelman.network.transfer.KaKaoTransfer;
import act.angelman.network.transfer.MessageTransfer;
import act.angelman.presentation.manager.ApplicationManager;

import static org.mockito.Mockito.mock;

public class AngelmanTestModule extends act.angelman.dagger.modules.AngelmanModule {

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

}
