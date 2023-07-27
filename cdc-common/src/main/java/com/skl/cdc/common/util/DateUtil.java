package com.skl.cdc.common.util;
import java.text.SimpleDateFormat;
import java.util.Date;
public class DateUtil {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String dateStr(Date date, String format) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

}
