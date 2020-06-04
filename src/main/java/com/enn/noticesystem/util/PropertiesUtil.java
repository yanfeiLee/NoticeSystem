package com.enn.noticesystem.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class PropertiesUtil {
    private static Properties props;

    static {
        loadProps();
    }

    synchronized static private void loadProps() {

        log.info("开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        try {
            // 取出application.properties文件参数
            InputStream ins = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties");
            Properties propss = new Properties();
            propss.load(ins);
            String active = propss.getProperty("spring.profiles.active");
            String propertiesName = "application-" + active + ".properties";

            in = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesName);
            props.load(in);
            //转码处理
            Set<Object> keyset = props.keySet();
            for (Object objectKey : keyset) {
                String key = (String) objectKey;
                //属性配置文件自身的编码
                String propertiesFileEncode = "utf-8";
                String newValue = new String(props.getProperty(key).getBytes(StandardCharsets.ISO_8859_1), propertiesFileEncode);
                props.setProperty(key, newValue);
            }
        } catch (Exception e) {
            log.error("配置文件加载异常：" + e.toString());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("properties文件流关闭出现异常");
            }
        }
        log.info("加载properties文件内容完成...........");
        log.info("properties文件内容：" + props);
    }

    public static String getProperty(String key) {
        if (null == props) {
            loadProps();
        }
        String value = props.getProperty(key.trim());
        if (null == value) {
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){
        if (null == props) {
            loadProps();
        }
        String value= props.getProperty(key.trim());
        if (null == value){
            value = defaultValue;
        }
        return value.trim();
    }
}