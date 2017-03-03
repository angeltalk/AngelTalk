package act.sds.samsung.angelman.data.repository.datastore;

import java.util.ArrayList;

import act.sds.samsung.angelman.domain.model.CategoryItemModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;

public interface CategoryDataStore {
    ArrayList<CategoryModel> getCategoryAllList();
    boolean deleteCategory(int category);
    ArrayList<CategoryItemModel> getCategoryAllIconList();
    ArrayList<CategoryItemModel> getCategoryAllBackgroundList();

    int saveNewCategoryItemAndReturnId(CategoryModel model);
}
