package com.enn.noticesystem.util;

import org.w3c.dom.ls.LSException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/28 10:03
 * Version: 1.0
 */
public class DateUtil {

    /**
     * @param
     * @return
     * @todo 获取前一天日期
     * @date 20/05/28 10:06
     */
    public static Map getYesterdayDate() {
        Map<String, String> mp = new HashMap<>();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        mp.put("year", String.valueOf(calendar.get(Calendar.YEAR)));
        mp.put("month", String.valueOf(calendar.get(Calendar.MONTH) + 1));
        mp.put("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        mp.put("week", getWeekString(calendar.get(Calendar.DAY_OF_WEEK)-1));
        return mp;
    }


    /**
    * @todo 数字和星期的映射
    * @date 20/05/28 11:25
    * @param
    * @return
    *
    */
    public static String getWeekString(Integer num) {
        String res = "";
        switch (num) {
            case 1:
                res = "星期一";
                break;
            case 2:
                res = "星期二";
                break;
            case 3:
                res = "星期三";
                break;
            case 4:
                res = "星期四";
                break;
            case 5:
                res = "星期五";
                break;
            case 6:
                res = "星期六";
                break;
            case 0:
                res = "星期日";
                break;
        }
        return res;
    }
}
