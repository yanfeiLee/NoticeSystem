package com.enn.noticesystem.service.impl;

import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.dao.api.ApiDao;
import com.enn.noticesystem.service.RuyiService;
import com.enn.noticesystem.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/22 17:34
 * Version: 1.0
 */
@Transactional
@Service
@Slf4j
public class RuyiServiceImpl implements RuyiService {

    @Autowired
    ApiDao apiDao;

    @Override
    public String listApis(String params) {
        String url = PropertiesUtil.getProperty("ruyi.api");
        Response response = apiDao.syncSend(true, url, RequestType.POST, params);
        String res = "";
        try {
            res = response.body().string();
        } catch (IOException e) {
            log.error("响应数据转String 异常");
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String listMetas(String url, String params) {
        return null;
    }

    @Override
    public String getContentByApi(String api, String[] meta) {
        return null;
    }
}
