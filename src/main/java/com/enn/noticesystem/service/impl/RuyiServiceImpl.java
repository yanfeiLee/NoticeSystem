package com.enn.noticesystem.service.impl;

import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.dao.api.ApiDao;
import com.enn.noticesystem.service.RuyiService;
import com.enn.noticesystem.util.CommonUtil;
import com.enn.noticesystem.util.JsonUtil;
import com.enn.noticesystem.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * @param
     * @return
     * @todo 根据配置获取如意地址：PORTOCOL://IP:PORT
     * @date 20/05/29 14:57
     */
    private String getRuyiAddr() {
        //读取配置
        String portocol = PropertiesUtil.getProperty("ruyi.protocol");
        String host = PropertiesUtil.getProperty("ruyi.host");
        String port = PropertiesUtil.getProperty("ruyi.port");
        return portocol + "://" + host + ":" + port;
    }


    @Override
    public Map<String, Object> listApis(Map<String,Object> params) {
        String url = getRuyiAddr() + PropertiesUtil.getProperty("ruyi.api.list");
        //指定topicId
        params.put("topicId",PropertiesUtil.getProperty("ruyi.api.topicId") );
        Response response = apiDao.syncSend(
                CommonUtil.isHttps(PropertiesUtil.getProperty("ruyi.protocol")),
                url,
                RequestType.POST,
                JsonUtil.getString(params));
        Map<String, Object> resMap = new HashMap<>();

        try {
            if (200 == response.code()) {
                Map<String, Object> responseMap = (Map) JsonUtil.getObj(response.body().string());
                if (responseMap.get("code").equals("200")) {
                    Map<String, Object> result_obj = (Map<String, Object>) responseMap.get("result_obj");
                    //获取可用状态api onlineApi
                    resMap.put("current", result_obj.get("pageNum").toString());
                    resMap.put("size", result_obj.get("pageSize").toString());
                    resMap.put("pages", result_obj.get("pages").toString());
                    resMap.put("total", result_obj.get("total").toString());
                    resMap.put("records", result_obj.get("list"));
                } else {
                    log.info("服务器响应正常，但获取api list失败");
                }
            } else {
                log.error("服务器响应异常");
            }
        } catch (IOException e) {
            log.error("响应数据转String 异常" + e.getMessage());
            e.printStackTrace();
        }
        return resMap;
    }

    @Override
    public Map<String, Object> listMetas(String params) {
        String url = getRuyiAddr() + PropertiesUtil.getProperty("ruyi.api.metaList");
        Response response = apiDao.syncSend(CommonUtil.isHttps(PropertiesUtil.getProperty("ruyi.protocol")),
                url,
                RequestType.POST,
                params);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("total", 0);
        resMap.put("metaList", "");
        try {
            if (200 == response.code()) {
                Map<String, Object> responseMap = JsonUtil.getMap(response.body().string());
                if (responseMap.get("code").toString().equals("200")) {
                    Map<String, Object> result_obj = (Map<String, Object>) responseMap.get("result_obj");
                    List<Map<String, Object>> metricsListMap = (List<Map<String, Object>>) result_obj.get("exitResult");
                    //过滤到主键
                    List<Map<String, Object>> showMetricsList = metricsListMap.stream()
                            .filter(obj -> !obj.get("columnName").toString().equals("pk_id"))
                            .filter(obj->!obj.get("columnDesc").toString().equals("主键"))
                            .collect(Collectors.toList());
                    resMap.put("total", showMetricsList.size());
                    resMap.put("metaList", showMetricsList);
                } else {
                    log.info("服务器响应正常，但获取metrics list失败");
                }
            } else {
                log.error("服务器响应异常");
            }
        } catch (IOException e) {
            log.error("响应数据转String 异常" + e.getMessage());
            e.printStackTrace();
        }
        return resMap;
    }

    @Override
    public Map<String, Object> getContentByApi(String params) {
        //todo 根据meta拼接请求body 解析请求类型 及 参数
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("metricsData", "");
        resMap.put("code", "");
        //todo 根据params 解析请求体

        String api = getRuyiAddr() + PropertiesUtil.getProperty("ruyi.api.dataList");
        Response response = apiDao.syncSend(CommonUtil.isHttps(PropertiesUtil.getProperty("ruyi.protocol")),
                api,
                RequestType.POST,
                params);
        try {
            if (200 == response.code()) {
                Map<String, Object> responseMap = JsonUtil.getMap(response.body().string());
                if (responseMap.get("code").toString().equals("200")) {
                    List<Map<String, Object>> metricsDataist = (List<Map<String, Object>>) responseMap.get("result_list");
                    resMap.put("metricsData", metricsDataist);
                    resMap.put("code", "200");
                } else {
                    log.info("服务器响应正常，但获取data list失败");
                }
            } else {
                log.error("服务器响应异常");
            }
        } catch (IOException e) {
            log.error("响应数据转String 异常" + e.getMessage());
            e.printStackTrace();
        }

        return resMap;
    }


}
