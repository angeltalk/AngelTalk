package angeltalk.plus.data.repository.datastore;

import java.util.List;

import angeltalk.plus.domain.model.CategoryItemModel;
import angeltalk.plus.domain.model.CategoryModel;

public interface CategoryDataStore {

    boolean deleteCategory(int category);
    int saveNewCategoryItemAndReturnId(CategoryModel model);
    List<CategoryModel> getCategoryAllList();
    List<CategoryItemModel> getCategoryAllIconList();
    List<CategoryItemModel> getCategoryAllBackgroundList();

}
