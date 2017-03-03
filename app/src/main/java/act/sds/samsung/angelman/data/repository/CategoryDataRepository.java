package act.sds.samsung.angelman.data.repository;

import android.content.Context;

import java.util.ArrayList;

import act.sds.samsung.angelman.data.repository.datastore.CategoryDataSqliteDataStore;
import act.sds.samsung.angelman.domain.model.CategoryItemModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;

public class CategoryDataRepository implements CategoryRepository {

    private Context context;

    public CategoryDataRepository(Context context) {
        this.context = context;
    }

    @Override
    public ArrayList<CategoryModel> getCategoryAllList() {
        CategoryDataSqliteDataStore dataStore = new CategoryDataSqliteDataStore(context);
        return dataStore.getCategoryAllList();
    }

    @Override
    public ArrayList<CategoryItemModel> getCategoryAllIconList() {
        CategoryDataSqliteDataStore dataStore = new CategoryDataSqliteDataStore(context);
        return dataStore.getCategoryAllIconList();
    }

    @Override
    public ArrayList<CategoryItemModel> getCategoryAllBackgroundList() {
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
