package angeltalk.plus.presentation.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.inject.Inject;

import angeltalk.plus.presentation.manager.ApplicationManager;

public class AbstractActivity extends AppCompatActivity {

    protected static Hashtable<String, String> restoreObject = new Hashtable<>();

    private static final String KEY_SEPARATE = "|";
    private static final String RESTORE_KEYS = "restore_keys";

    @Inject
    ApplicationManager applicationManager;


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
