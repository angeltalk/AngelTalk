package angeltalk.plus.dagger.modules;

import javax.inject.Singleton;

import angeltalk.plus.data.repository.CardDataRepository;
import angeltalk.plus.data.repository.CategoryDataRepository;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.domain.repository.CategoryRepository;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AngelmanModule {

    @Binds
    @Singleton
    public abstract CardRepository bindCardRepository(CardDataRepository impl);

    @Binds
    @Singleton
    public abstract CategoryRepository bindCategoryRepository(CategoryDataRepository impl);
}
