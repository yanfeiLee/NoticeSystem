package com.enn.noticesystem.util;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/22 17:41
 * Version: 1.0
 */
@Slf4j
public class PropertiesUtil {
    private static Properties props;

    static {
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties"),"UTF-8"));
        }catch (IOException e){
            log.error("导入配置文件异常");
            e.printStackTrace();
        }
    }
    public static String getProperty(String key){
        String value= props.getProperty(key.trim());
        if (null == value){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){
        String value= props.getProperty(key.trim());
        if (null == value){
            value = defaultValue;
        }
        return value.trim();
    }

}
