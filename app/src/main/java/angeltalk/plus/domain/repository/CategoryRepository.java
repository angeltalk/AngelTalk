package angeltalk.plus.domain.repository;

import java.util.List;

import angeltalk.plus.domain.model.CategoryItemModel;
import angeltalk.plus.domain.model.CategoryModel;

public interface CategoryRepository {
    boolean deleteCategory(int category);
    int saveNewCategoryItemAndReturnId(CategoryModel model);
    List<CategoryModel> getCategoryAllList();
    List<CategoryItemModel> getCategoryAllIconList();
    List<CategoryItemModel> getCategoryAllBackgroundList();
}
