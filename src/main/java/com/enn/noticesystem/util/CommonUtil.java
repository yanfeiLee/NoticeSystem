package com.enn.noticesystem.util;

import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/29 14:59
 * Version: 1.0
 */
public class CommonUtil {

    /**
    * @todo 判断协议是否为https协议
    * @date 20/05/29 15:00
    * @param
    * @return
    *
    */
    public static Boolean isHttps(String portocol){
        if("http".equals(portocol)){
            return false;
        }else{
            return true;
        }
    }
}
