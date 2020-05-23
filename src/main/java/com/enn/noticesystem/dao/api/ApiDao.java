package com.enn.noticesystem.dao.api;

import com.enn.noticesystem.constant.RequestType;
import okhttp3.Response;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/18 18:00
 * Version: 1.0
 */
public interface ApiDao {
    /**
    * @todo 从接口中获取数据，同步处理
    * @date 20/05/18 18:03
    * @param isHttps 是否为https请求
    * @param url 请求地址
    * @param reqType 请求类型
    * @param reqParams 请求参数
    * @return
    *
    */
    Response syncSend(Boolean isHttps,String url, RequestType reqType, String reqParams);
}
