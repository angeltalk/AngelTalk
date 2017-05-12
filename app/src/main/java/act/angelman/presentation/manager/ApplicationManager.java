package act.angelman.presentation.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.util.KakaoParameterException;

import act.angelman.R;
import act.angelman.domain.model.CategoryModel;
import act.angelman.presentation.custom.ChildModeManager;
import act.angelman.presentation.service.ScreenService;
import act.angelman.presentation.util.ResourcesUtil;

public class ApplicationManager {

    private static final String CATEGORY_MODEL_TITLE = "categoryModelTitle";
    private static final String CATEGORY_MODEL_ICON = "categoryModelIcon";
    private static final String CATEGORY_MODEL_COLOR = "categoryModelColor";
    private static final String CATEGORY_MODEL_INDEX = "categoryModelIndex";
    private static final String CURRENT_CARD_INDEX = "currentCardIndex";
    private static final String CHILD_MODE = "childMode";

    private SharedPreferences preferences;
    private ChildModeManager childModeManager;
    private Context context;
    private KakaoLink kakaoLink;

    public ApplicationManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.childModeManager = new ChildModeManager(context);
        try {
            this.kakaoLink = KakaoLink.getKakaoLink(context);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

    public void setCategoryModel(CategoryModel categoryModel){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(CATEGORY_MODEL_TITLE, categoryModel.title)
                .putInt(CATEGORY_MODEL_INDEX, categoryModel.index)
                .putInt(CATEGORY_MODEL_ICON, categoryModel.icon)
                .putInt(CATEGORY_MODEL_COLOR, categoryModel.color)
                .apply();
    }

    public CategoryModel getCategoryModel(){
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.title = preferences.getString(CATEGORY_MODEL_TITLE, null);
        categoryModel.index = preferences.getInt(CATEGORY_MODEL_INDEX, -1);
        categoryModel.icon = preferences.getInt(CATEGORY_MODEL_ICON, -1);
        categoryModel.color = preferences.getInt(CATEGORY_MODEL_COLOR, -1);
        return categoryModel;
    }

    public void setCurrentCardIndex(int currentCardIndex){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(CURRENT_CARD_INDEX, currentCardIndex).apply();
    }

    public int getCurrentCardIndex(){
        return preferences.getInt(CURRENT_CARD_INDEX,0);
    }


    @ResourcesUtil.BackgroundColors
    public int getCategoryModelColor(){
        return this.getCategoryModel().color;
    }

    public void setCategoryBackground(View rootView,@ResourcesUtil.BackgroundColors int color){
        ResourcesUtil.setViewBackground(rootView, color, context);
    }

    public void setChildMode(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(CHILD_MODE, true);
        if(!isServiceRunningCheck()) {
            Intent screenService = new Intent(context, ScreenService.class);
            context.startService(screenService);
        }
        edit.commit();
        Toast.makeText(context, R.string.inform_show_child_mode, Toast.LENGTH_LONG).show();
    }

    public void setNotChildMode(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(CHILD_MODE, false);
        ActivityManager manager = ((ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE));
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(ScreenService.class.getCanonicalName())) {
                Intent stop = new Intent();
                stop.setComponent(service.service);
                context.stopService(stop);
            }
        }
        edit.commit();
        Toast.makeText(context, R.string.inform_hide_child_mode, Toast.LENGTH_LONG).show();
    }

    public boolean isChildMode(){
        return preferences.getBoolean(CHILD_MODE, true);
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(ScreenService.class.getCanonicalName())) {
                return true;
            }
        }
        return false;
    }

    public void changeChildMode(boolean mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        if (mode) {
            if (!isServiceRunningCheck()) {
                Intent screenService = new Intent(context, ScreenService.class);
                context.startService(screenService);
            }
            edit.putBoolean(CHILD_MODE, true).apply();
            Toast.makeText(context, R.string.inform_show_child_mode, Toast.LENGTH_LONG).show();
        } else {
            ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (service.service.getClassName().contains(ScreenService.class.getCanonicalName())) {
                    Intent stop = new Intent();
                    stop.setComponent(service.service);
                    context.stopService(stop);
                }
            }
            edit.putBoolean(CHILD_MODE, false).apply();
            Toast.makeText(context, R.string.inform_hide_child_mode, Toast.LENGTH_LONG).show();
        }
    }

    @VisibleForTesting
    public ChildModeManager getChildModeManager() {
        return childModeManager;
    }

    public KakaoLink getKakaoLink() {
        return kakaoLink;
    }

    public void makeChildView(){
        childModeManager.removeAllView();
        childModeManager.createAndAddCategoryMenu();
    }

    public void setNotFirstLaunched() {
        if(preferences.getBoolean(ApplicationConstants.FIRST_LAUNCH, true)) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(ApplicationConstants.FIRST_LAUNCH, false);
            edit.commit();
        }
    }

    public boolean isFirstLaunched(){
        return preferences.getBoolean(ApplicationConstants.FIRST_LAUNCH, true);
    }

}
