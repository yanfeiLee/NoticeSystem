package com.enn.noticesystem.util;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/29 14:59
 * Version: 1.0
 */
public class CommonUtil {

    /**
     * @param
     * @return
     * @todo 判断协议是否为https协议
     * @date 20/05/29 15:00
     */
    public static Boolean isHttps(String portocol) {
        if ("http".equals(portocol)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
    * @todo 验证id
    * @date 20/06/07 10:23
    * @param
    * @return
    *
    */
    public static Map<String,Object> idValidate(String params){
        Map<String,Object> paramMap= (Map<String, Object>) JsonUtil.getObj(params);
        Map<String,Object> res= new HashMap<>();
        res.put("id", "");
        res.put("info", "");
        if(!paramMap.containsKey("id")){
            res.put("info","请求参数错误");
        }else{
            String id = paramMap.get("id").toString();
            if(!isNumeric(id)){
                res.put("info","id值不合法");
            }else{
                res.put("id", id);
            }
        }
        return res;
    }
}
