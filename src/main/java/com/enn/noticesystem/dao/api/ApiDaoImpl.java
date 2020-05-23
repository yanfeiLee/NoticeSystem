package com.enn.noticesystem.dao.api;

import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.util.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/18 18:01
 * Version: 1.0
 */
@Slf4j
@Component
public class ApiDaoImpl implements ApiDao {


    @Override
    public Response syncSend(Boolean isHttps, String url, RequestType reqType, String reqParams) {
        OkHttpClient client = null;
        Response response = null;
        Request request = null;

        try {
            if (isHttps) {
                client = OkHttpClientUtil.getHttpsClient();
            } else {
                client = OkHttpClientUtil.getHttpClient();
            }
        } catch (Exception e) {
            log.error("获取httpsClient异常");
            e.printStackTrace();
        }


        log.info("发送同步请求开始");
        //构造请求内容
        Request.Builder reqBuilder = new Request.Builder().url(url);
        switch (reqType) {
            case GET:
                reqParams = "";
                request = reqBuilder.get().build();
                break;
            case POST:
                MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, reqParams);
                request = reqBuilder.post(requestBody).build();
                break;
        }

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            log.error("发送同步请求失败");
            e.printStackTrace();
            return null;
        }
        log.info("发送同步请求成功，并获取到数据");
        return response;
    }
}
