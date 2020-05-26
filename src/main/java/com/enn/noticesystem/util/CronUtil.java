package com.enn.noticesystem.util;

import java.util.HashMap;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/26 10:36
 * Version: 1.0
 */
public class CronUtil {

    /**
    * @todo 解析时间 （08:09:00）->(00 09 08)
    * @date 20/05/26 10:59
    * @param
    * @return
    */
    private static String parseTime(String timeStr){
        StringBuilder stringBuilder = new StringBuilder();
        String[] split = timeStr.trim().split(":");
        for (int i=split.length-1; i>=0;i--){
            stringBuilder.append(split[i].trim()+" ");
        }
        return stringBuilder.toString();
    }
    /**
    * @todo {"period":"day","time":"08:08:00","repeat":""} 根据用户选择（自然语言），生成cron表达式
    * @date 20/05/26 11:17
    * @param period 执行周期（日、周、月）
    * @param timeStr 任务具体执行时间
    * @param optionalDate 可选参数，（周或月周期中具体的周几或几日）
    * @return
    *
    */
    public static String genCronSingle(String period,String timeStr,String... optionalDate){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parseTime(timeStr));
        if("day".equals(period)){
            stringBuilder.append("* * ? *");
        }else if("week".equals(period)){
            stringBuilder.append("? * ");
            stringBuilder.append(optionalDate[0].trim());
            stringBuilder.append(" *");
        }else if("month".equals(period)){
            stringBuilder.append(optionalDate[0].trim());
            stringBuilder.append(" * ? *");
        }
        return stringBuilder.toString();
    }

    /**
    * @todo 解析cron表达式为自然语言{"period":"day","time":"08:08:00","repeat":""}
    * @date 20/05/26 13:14
    * @param
    * @return
    *
    */
    public static String parseCronSingle(String cronExp){
        HashMap<String, String> res = new HashMap<>();
        String[] split = cronExp.split(" ");
        res.put("time", split[0]+":"+split[1]+":"+split[2]);
        if(!"?".equals(split[5]) && "?".equals(split[3])){
            //周
            res.put("period", "week");
            res.put("repeat", split[5]);
        }else{
            if("*".equals(split[3])){
                //日
                res.put("period", "day");
                res.put("repeat", "");
            }else{
                //月
                res.put("period", "month");
                res.put("repeat", split[3]);
            }
        }
        return JsonUtil.getString(res);
    }

}
