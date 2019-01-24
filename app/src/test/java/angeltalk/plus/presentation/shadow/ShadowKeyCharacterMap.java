package angeltalk.plus.presentation.shadow;

import android.view.KeyCharacterMap;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(KeyCharacterMap.class)
public class ShadowKeyCharacterMap extends org.robolectric.shadows.ShadowKeyCharacterMap{

    @Implementation
    public static boolean deviceHasKey(int keyCode) {
        return true;
    }
}