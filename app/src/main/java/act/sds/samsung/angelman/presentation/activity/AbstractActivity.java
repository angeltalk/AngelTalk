package act.sds.samsung.angelman.presentation.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Enumeration;
import java.util.Hashtable;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

public class AbstractActivity extends AppCompatActivity {

    protected static Hashtable<String, String> restoreObject = new Hashtable<>();

    private static final String KEY_SEPARATE = "|";
    private static final String RESTORE_KEYS = "restore_keys";
    @ResourcesUtil.BackgroundColors
    protected int categoryColor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryColor = ((AngelmanApplication) getApplicationContext()).getCategoryModel().color;
    }

    protected void setCategoryBackground(@IdRes int resId){
        View rootContainer = findViewById(resId);
        ResourcesUtil.setViewBackground(rootContainer, categoryColor, getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Enumeration keys = restoreObject.keys();
        String keyString = null;
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = restoreObject.get(key);

            savedInstanceState.putString(key, value);
            keyString += "|" + key;
        }
        savedInstanceState.putString(RESTORE_KEYS, keyString);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String keyString = savedInstanceState.getString(RESTORE_KEYS);

        if (keyString == null) return;

        String[] keyList = keyString.split(KEY_SEPARATE);

        for (String key : keyList) {
            if (key != null && key.length() > 0) {
                String restoreValue = savedInstanceState.getString(key);
                restoreObject.put(key, restoreValue);
            }
        }
    }

    public int getCategoryColor() {
        return categoryColor;
    }
}
