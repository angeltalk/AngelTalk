package act.angelman.data.repository;

import android.content.Context;

import java.util.List;

import act.angelman.data.repository.datastore.CategoryDataSqliteDataStore;
import act.angelman.domain.model.CategoryItemModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CategoryRepository;

public class CategoryDataRepository implements CategoryRepository {

    private Context context;

    public CategoryDataRepository(Context context) {
        this.context = context;
    }

    @Override
    public List<CategoryModel> getCategoryAllList() {
        CategoryDataSqliteDataStore dataStore = new CategoryDataSqliteDataStore(context);
        return dataStore.getCategoryAllList();
    }

    @Override
    public List<CategoryItemModel> getCategoryAllIconList() {
        CategoryDataSqliteDataStore dataStore = new CategoryDataSqliteDataStore(context);
        return dataStore.getCategoryAllIconList();
    }

    @Override
    public List<CategoryItemModel> getCategoryAllBackgroundList() {
        CategoryDataSqliteDataStore dataStore = new CategoryDataSqliteDataStore(context);
        return dataStore.getCategoryAllBackgroundList();
    }

    @Override
    public int saveNewCategoryItemAndReturnId(CategoryModel model) {
        CategoryDataSqliteDataStore dataStore = new CategoryDataSqliteDataStore(context);
        return dataStore.saveNewCategoryItemAndReturnId(model);
    }

    @Override
    public boolean deleteCategory(int category) {
        CategoryDataSqliteDataStore dataStore = new CategoryDataSqliteDataStore(context);
        return dataStore.deleteCategory(category);
    }

}
