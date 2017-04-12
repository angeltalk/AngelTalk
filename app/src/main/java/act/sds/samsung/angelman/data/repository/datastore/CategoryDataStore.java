package act.sds.samsung.angelman.data.repository.datastore;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CategoryItemModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;

public interface CategoryDataStore {

    boolean deleteCategory(int category);
    int saveNewCategoryItemAndReturnId(CategoryModel model);
    List<CategoryModel> getCategoryAllList();
    List<CategoryItemModel> getCategoryAllIconList();
    List<CategoryItemModel> getCategoryAllBackgroundList();

}
