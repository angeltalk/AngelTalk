package act.sds.samsung.angelman.presentation.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String DEFAULT_PATTERN = "yyyyMMdd_HHmmss";

    public static String getDateNow() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
        return dateFormat.format(date);
    }
}
