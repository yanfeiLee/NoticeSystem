package com.enn.noticesystem.util;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.io.StringWriter;
import java.util.Map;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/28 10:35
 * Version: 1.0
 */
@Slf4j
public class TemplateUtil {

    /**
    * @todo 将模板中的${year}等变量解析为值
    * @date 20/05/28 10:39
    * @param
    * @return
    *
    */
    public static String replaceVar(String template, Map data){
        try {
            //定义StringTemplateLoader
            StringTemplateLoader loader = new StringTemplateLoader();
            loader.putTemplate("content", template);

            //定义Configuration
            Configuration configuration = new Configuration();
            configuration.setTemplateLoader(loader);

            //定义Template
            Template tpl = configuration.getTemplate("content");

            StringWriter writer = new StringWriter();
            tpl.process(data, writer);
            return writer.toString();
        } catch (Exception e) {
            log.info("模板解析异常:"+e.getMessage());
            return "";
        }

    }
}
