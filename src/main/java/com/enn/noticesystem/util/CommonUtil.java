package com.enn.noticesystem.util;

import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.constant.ResponseType;
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


    /**
    * @todo 依据请求类型code，返回请求类型
    * @date 20/06/12 17:40
    * @param
    * @return
    *
    */
    public static RequestType getRequstTypeByCode(String code){
        RequestType rt=RequestType.GET;
        if(RequestType.POST.getCode().toString().equals(code)){
            rt =  RequestType.POST;
        }else if(RequestType.GET.getCode().toString().equals(code)){

        }else{

        }
        return rt;
    }

    /**
    * @todo 依据响应code,判断响应类型
    * @date 20/06/12 17:42
    * @param
    * @return
    *
    */
    public static ResponseType getResponseTypeByCode(String code){
        ResponseType rt = ResponseType.JSON;
        if(ResponseType.XML.getCode().toString().equals(code)){
            rt = ResponseType.XML;
        }else{

        }


        return rt;
    }
}
