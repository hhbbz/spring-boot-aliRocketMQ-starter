package com.zwx.boot.rocketMQ.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hhbbz on 2017/11/9.
 * @Explain:
 */

public class DateUtil {
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * Transfer date string to timestamp
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Long getTimestampByDateString(String dateString) throws ParseException {
        Long time = null;
        if (AssertValue.isNotEmpty(dateString)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(dateString);
            time = date.getTime();
        }
        return time;
    }

    /**
     * Format date to String
     *
     * @param date
     * @return
     */
    public static String formatDateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
