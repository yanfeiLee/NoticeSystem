package com.enn.noticesystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.management.ObjectName;
import java.util.Map;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 16:28
 * Version: 1.0
 */
public class JsonUtil {

    /**
    * @todo 对象转为JSON字符串
    * @date 20/05/20 16:29
    * @param
    * @return
    *
    */
    public static String getString(Object obj){
        return JSON.toJSONString(obj);
    }

    /**
     *  @author liyanfei
     *  @date 20/05/20 16:34
     *  @description Json字符串转对象
     *
     */
    public static Object getObj(String jsonStr){
        if(checkJsonStr(jsonStr)){
            return JSON.parseObject(jsonStr);
        }else{
            return null;
        }
    }


    public static Map<String,Object> getMap(String jsonStr){
        if(checkJsonStr(jsonStr)){
             return (Map<String, Object>) JSONObject.parse(jsonStr);
        }else{
            return null;
        }
    }
    /**
     *  @author liyanfei
     *  @date 20/05/20 17:16
     *  @description 校验json 字符串合法性
     *
     */
    private static boolean checkJsonStr(String jsonStr){
        return true;
    }
}
