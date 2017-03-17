package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import act.sds.samsung.angelman.domain.model.CategoryModel;

import static act.sds.samsung.angelman.AngelmanApplication.PRIVATE_PREFERENCE_NAME;

public class ApplicationManager {

    private static final String CATEGORY_MODEL_TITLE = "categoryModelTitle";
    private static final String CATEGORY_MODEL_ICON = "categoryModelIcon";
    private static final String CATEGORY_MODEL_COLOR = "categoryModelColor";
    private static final String CATEGORY_MODEL_INDEX = "categoryModelIndex";
    private SharedPreferences preferences;

    public ApplicationManager(Context context) {
        this.preferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setCategoryModel(CategoryModel categoryModel){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(CATEGORY_MODEL_TITLE, categoryModel.title);
        edit.putInt(CATEGORY_MODEL_INDEX, categoryModel.index);
        edit.putInt(CATEGORY_MODEL_ICON, categoryModel.icon);
        edit.putInt(CATEGORY_MODEL_COLOR, categoryModel.color);
        edit.commit();
    }

    public CategoryModel getCategoryModel(){
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.title = preferences.getString(CATEGORY_MODEL_TITLE, null);
        categoryModel.index = preferences.getInt(CATEGORY_MODEL_INDEX, -1);
        categoryModel.icon = preferences.getInt(CATEGORY_MODEL_ICON, -1);
        categoryModel.color = preferences.getInt(CATEGORY_MODEL_COLOR, -1);
        return categoryModel;
    }
    //@ResourcesUtil.BackgroundColors
    //int cate

    @ResourcesUtil.BackgroundColors
    public int getCategoryModelColor(){
        return this.getCategoryModel().color;
    }

    public void setCategoryBackground(Context context, View rootView,@ResourcesUtil.BackgroundColors int color){
        ResourcesUtil.setViewBackground(rootView, color, context);
    }
}
