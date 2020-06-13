package com.enn.noticesystem.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/28 10:03
 * Version: 1.0
 */
@Slf4j
public class DateUtil {

    public static final String DT_FORMAT="yyyy-MM-dd";


    /**
     * @param
     * @return
     * @todo 获取前一天日期,
     * @date 20/05/28 10:06
     */
    public static Map<String,String> getCurrDayDateMp(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DT_FORMAT);
        Map<String, String> mp = new HashMap<>();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(dateStr));

        mp.put("year", String.valueOf(calendar.get(Calendar.YEAR)));
        mp.put("month", String.valueOf(calendar.get(Calendar.MONTH) + 1));
        mp.put("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        mp.put("week", getWeekString(calendar.get(Calendar.DAY_OF_WEEK)-1));
        return mp;
    }

    public static Map<String,Map<String,String>> getFromToMp(String from,String to){
        Map<String, Map<String,String>> mp = new HashMap<>();
        try {
            mp.put("from", getCurrDayDateMp(from));
            mp.put("to", getCurrDayDateMp(to));
        }catch (Exception e){
            log.error("日期解析异常");
        }
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
    


//    public static void main(String[] args) {
//        Map<String, String> rs = getDateRangeByCurrentTime(-1, -2);
//        System.out.println(rs.get("start"));
//        System.out.println(rs.get("end"));
//
//
//    }

    public static Map<String,String> getDateRangeByCurrentTime(Integer from,Integer to){
        String fmt="yyyy-MM-dd";
        Map<String,String> res = new HashMap<>();
        Calendar curr = Calendar.getInstance();
        curr.add(Calendar.DATE, from);
        res.put("start", formatDate(curr.getTime(), fmt));

        curr = Calendar.getInstance();
        curr.add(Calendar.DATE,to);
        res.put("end", formatDate(curr.getTime(), fmt));
        return res;
    }

    /**
    * @todo 格式化日期
    * @date 20/06/12 18:29
    * @param
    * @return
    *
    */
    public static String formatDate(Date date,String fmt){
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        String res = sdf.format(date);
        return res;
    }
}
