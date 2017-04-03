package act.sds.samsung.angelman.dagger.modules;

import android.content.Context;

import act.sds.samsung.angelman.network.transfer.CardTransfer;
import act.sds.samsung.angelman.network.transfer.KaKaoTransfer;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;

import static org.mockito.Mockito.mock;

public class AngelmanTestModule extends AngelmanModule {

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


}
