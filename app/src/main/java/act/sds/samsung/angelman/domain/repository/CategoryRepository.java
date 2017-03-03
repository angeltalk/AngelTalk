package act.sds.samsung.angelman.domain.repository;

import java.util.ArrayList;

import act.sds.samsung.angelman.domain.model.CategoryItemModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;

public interface CategoryRepository {
    ArrayList<CategoryModel> getCategoryAllList();
    boolean deleteCategory(int category);

    ArrayList<CategoryItemModel> getCategoryAllIconList();
    ArrayList<CategoryItemModel> getCategoryAllBackgroundList();

    int saveNewCategoryItemAndReturnId(CategoryModel model);
}
