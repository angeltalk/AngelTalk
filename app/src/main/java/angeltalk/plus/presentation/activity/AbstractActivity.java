package angeltalk.plus.presentation.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

import javax.inject.Inject;

import angeltalk.plus.presentation.manager.ApplicationManager;

public class AbstractActivity extends AppCompatActivity {

    protected static Hashtable<String, String> restoreObject = new Hashtable<>();

    private static final String KEY_SEPARATE = "|";
    private static final String RESTORE_KEYS = "restore_keys";

    @Inject
    ApplicationManager applicationManager;


    @Override
    protected void attachBaseContext(Context base) {
        Locale locale = new Locale("ko");
        Locale.setDefault(locale);
        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.setLocale(locale);
        super.attachBaseContext(base.createConfigurationContext(config));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


}
