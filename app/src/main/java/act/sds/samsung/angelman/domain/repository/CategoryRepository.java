package act.sds.samsung.angelman.domain.repository;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CategoryItemModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;

public interface CategoryRepository {
    List<CategoryModel> getCategoryAllList();
    boolean deleteCategory(int category);

    List<CategoryItemModel> getCategoryAllIconList();
    List<CategoryItemModel> getCategoryAllBackgroundList();

    int saveNewCategoryItemAndReturnId(CategoryModel model);
}
