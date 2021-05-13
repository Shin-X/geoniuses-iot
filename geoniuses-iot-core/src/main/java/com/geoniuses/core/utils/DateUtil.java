package com.geoniuses.core.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author ：zyf
 * @date ：2020/8/12 11:56
 */
public class DateUtil {
    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String getNowDateSync() {
        return new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }
}
