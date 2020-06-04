package com.enn.noticesystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.dao.api.ApiDao;
import com.enn.noticesystem.domain.MsgTemplate;
import com.enn.noticesystem.service.MsgTemplateService;
import com.enn.noticesystem.service.RuyiService;
import com.enn.noticesystem.util.JsonUtil;
import com.enn.noticesystem.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 18:06
 * Version: 1.0
 */
@RestController
@RequestMapping("template")
@Slf4j
public class MsgTemplateController {

    @Autowired
    MsgTemplateService msgTemplateService;
    @Autowired
    RuyiService ruyiService;

    private HashMap<Object, Object> res = new HashMap<>();

    @GetMapping("addV/type/{type}/p/{pageNo}/s/{size}")
    public String addV(@PathVariable("type") String type,@PathVariable("pageNo") String pageNo,
                       @PathVariable("size") String size) {
        Map<String, Object> resMap = new HashMap<>();
        //准备添加页面需要的数据
        if (type.equals("1")) {
            //机器人
            //请求如意提供 指标的 所有接口
            //请求参数
            Map<String,Object> params = new HashMap<>();
            params.put("pageNum", pageNo);
            params.put("pageSize", size);

            resMap = ruyiService.listApis(params);
            //todo 解析响应体 获取api名称、请求类型、查询数据所需参数 按需返回

        } else if (type.equals("2")) {

        } else if (type.equals("3")) {

        } else if (type.equals("4")) {

        }
        res.clear();
        res.put("res", resMap);
        return JsonUtil.getString(res);
    }

    @GetMapping("addV/type/{type}/api/{id}")
    public String addV(@PathVariable("type") String type, @PathVariable("id") String id) {
        res.clear();
        if (type.equals("1")) {
            //请求参数
            String reqParams = "{\"id\":" + id + "}";
            //获取meta列表[{"meta":"pv","metaName":"页面访问量"},{}]
            Map<String, Object> metricsMap = ruyiService.listMetas(reqParams);
            res.put("metas", metricsMap);
        }
        return JsonUtil.getString(res);
    }

    @PostMapping("add")
    public String add(@RequestBody MsgTemplate msgTemplate) {
        //数据校验

        //获取用户id
        msgTemplate.setCreatorId(1);

        res.clear();
        Integer resAdd = msgTemplateService.add(msgTemplate);
        if (resAdd != -1) {
            res.put("res", true);
            res.put("id", resAdd);
        } else {
            res.put("res", false);
        }
        return JsonUtil.getString(res);
    }

    @PostMapping("update")
    public String update(@RequestBody MsgTemplate msgTemplate) {

        res.clear();
        res.put("res", msgTemplateService.update(msgTemplate));
        return JsonUtil.getString(res);
    }

    @PostMapping("del/{id}")
    public String del(@PathVariable("id") String id) {

        res.clear();
        res.put("res", msgTemplateService.delete(Integer.valueOf(id)));
        return JsonUtil.getString(res);
    }

    @GetMapping("id/{id}")
    public String getTemplateById(@PathVariable("id") String id) {

        res.clear();
        res.put("content", msgTemplateService.getTemplateById(id));
        return JsonUtil.getString(res);
    }

    @GetMapping("type/{type}/name/{name}/p/{pageNo}/s/{size}")
    public String getTemplateByName(@PathVariable("type") String type,@PathVariable("name") String name,@PathVariable("pageNo") String pageNo,
                                    @PathVariable("size") String size) {
        //获取当前登录用户
        String userId = "1";
        Page<MsgTemplate> msgTemplates = new Page<>(Long.valueOf(pageNo), Long.valueOf(size));
        IPage<MsgTemplate> page = msgTemplateService.listTemplatesByName(userId, type, name, msgTemplates);
        return JsonUtil.getString(page);
    }


    @GetMapping("type/{type}/p/{pageNo}/s/{size}")
    public String getTemplatesByPage(@PathVariable("type") String type, @PathVariable("pageNo") String pageNo,
                                     @PathVariable("size") String size) {
        //获取用户id
        String userId = "1";
        Page<MsgTemplate> msgTemplatePage = new Page<>(Long.valueOf(pageNo), Long.valueOf(size));
        IPage<MsgTemplate> page = msgTemplateService.listPagesByType(msgTemplatePage, userId, type);
        return JsonUtil.getString(page);
    }

    //test
    @GetMapping("testRYData")
    public String getTest(){
        String params = "{\n" +
                "    \"apiId\": 3,\n" +
                "    \"datasourceName\": \"RDS_MYSQL\",\n" +
                "    \"tableName\": \"rds_yun_master_month_every_day_consume_amount\",\n" +
                "    \"queryType\": \"normal\",\n" +
                " \n" +
                "    \"enterParam\": [\n" +
                "        {\n" +
                "            \"columnName\": \"company_id\",\n" +
                "            \"conditionType\": \"equals\",\n" +
                "            \"value\": \"90056\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"outParam\": [\n" +
                "        {\n" +
                "            \"columnName\": \"company_name\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"columnName\": \"consume_date\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"columnName\": \"consume_amount\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"columnName\": \"pk_id\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        Map<String, Object> contentByApi = ruyiService.getContentByApi(params);
        return  JsonUtil.getString(contentByApi);
    }
}

