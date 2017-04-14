package act.angelman.domain.repository;

import java.util.List;

import act.angelman.domain.model.CategoryItemModel;
import act.angelman.domain.model.CategoryModel;

public interface CategoryRepository {
    boolean deleteCategory(int category);
    int saveNewCategoryItemAndReturnId(CategoryModel model);
    List<CategoryModel> getCategoryAllList();
    List<CategoryItemModel> getCategoryAllIconList();
    List<CategoryItemModel> getCategoryAllBackgroundList();
}
