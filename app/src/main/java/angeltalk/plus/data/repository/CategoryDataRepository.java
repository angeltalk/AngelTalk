package angeltalk.plus.data.repository;

import android.content.Context;

import java.util.List;

import angeltalk.plus.data.repository.datastore.CategoryDataSqliteDataStore;
import angeltalk.plus.data.repository.datastore.CategoryDataStore;
import angeltalk.plus.domain.model.CategoryItemModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CategoryRepository;

public class CategoryDataRepository implements CategoryRepository {

    private Context context;
    CategoryDataStore dataStore;

    public CategoryDataRepository(Context context) {
        this.context = context;
        this.dataStore = new CategoryDataSqliteDataStore(context);
    }

    @Override
    public List<CategoryModel> getCategoryAllList() {
        return dataStore.getCategoryAllList();
    }

    @Override
    public List<CategoryItemModel> getCategoryAllIconList() {
        return dataStore.getCategoryAllIconList();
    }

    @Override
    public List<CategoryItemModel> getCategoryAllBackgroundList() {
        return dataStore.getCategoryAllBackgroundList();
    }

    @Override
    public int saveNewCategoryItemAndReturnId(CategoryModel model) {
        return dataStore.saveNewCategoryItemAndReturnId(model);
    }

    @Override
    public boolean deleteCategory(int category) {
        return dataStore.deleteCategory(category);
    }

}
