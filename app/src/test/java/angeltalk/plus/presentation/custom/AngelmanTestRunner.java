package angeltalk.plus.presentation.custom;

/**
 * Created by actmember on 11/11/16.
 */

import android.os.SystemClock;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

import java.util.Locale;
import java.util.TimeZone;

public class AngelmanTestRunner {

    public static class WithKorean extends RobolectricTestRunner {
        public WithKorean(Class<?> testClass) throws InitializationError {
            super(testClass);
            Locale.setDefault(Locale.KOREA);

            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
            SystemClock.setCurrentTimeMillis(1457386200000L); // 2016-3-8 06:30:00 Carl's birthday alarm
        }
    }

    public static class WithEnglish extends RobolectricTestRunner {
        public WithEnglish(Class<?> testClass) throws InitializationError {
            super(testClass);
            Locale.setDefault(Locale.ENGLISH);
        }
    }
}