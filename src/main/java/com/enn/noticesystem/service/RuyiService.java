package com.enn.noticesystem.service;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/22 17:12
 * Version: 1.0
 */
/**
 *  调用如意平台提供的api接口服务
 */
public interface RuyiService {

    /**
    * @todo 列出如意平台提供的接口服务
    * @date 20/05/22 17:14
    * @param
    * @return
    *
    */
    String listApis(String params);

    /**
    * @todo 列出某个api下的资源
    * @date 20/05/22 17:25
    * @param
    * @return
    *
    */
    String listMetas(String url,String params);

    /**
    * @todo 根据api地址，及指定资源，获取对应数据
    * @date 20/05/22 17:30
    * @param
    * @return
    */
    String getContentByApi(String api,String[] meta);
}