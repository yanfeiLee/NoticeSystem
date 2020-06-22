package com.enn.noticesystem.service.job;

import com.enn.noticesystem.constant.PushChannelTypeEnum;
import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.constant.TaskExecStatusEnum;
import com.enn.noticesystem.constant.WebhookTemplateTypeEnum;
import com.enn.noticesystem.dao.api.ApiDao;
import com.enn.noticesystem.domain.Msg;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.MsgService;
import com.enn.noticesystem.service.RuyiService;
import com.enn.noticesystem.service.ScheduleJobService;
import com.enn.noticesystem.util.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/26 14:14
 * Version: 1.0
 */
@Component("WebhookJob")
@Transactional
@DisallowConcurrentExecution
@Slf4j
public class WebhookJob {

    private Msg msg;
    private ScheduleJob job;

    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    ApiDao apiDao;
    @Autowired
    MsgService msgService;
    @Autowired
    RuyiService ruyiService;

    /**
     * @param
     * @return 每个机器人发送的消息不能超过20条/分钟。
     * @todo 执行推送任务，并将推送结果写入库
     * @date 20/05/26 14:21
     */
    public void execute(String id) {
        log.info("执行webHook推送任务。。。");
        job = scheduleJobService.getScheduleJobById(Integer.valueOf(id));
        ScheduleJobVO jobVO = scheduleJobService.getScheduleJobVOById(Integer.valueOf(id));

        log.info("执行任务id=" + id + "的任务,名称为:" + jobVO.getName());

//        生成消息id
        msg = createMsg(id);
//        拉取数据

        //解析模板的api的请求类型，响应类型及指标
        Map<String, Object> templateParseMp = parseTemplateParams(jobVO.getTemplateRobotPushTemplate());
        String reqParams = JsonUtil.getString(templateParseMp.get("reqParams"));
        String reqType = templateParseMp.get("reqType").toString();
        String resType = templateParseMp.get("resType").toString();
        log.info("获取数据时，根据模板生成的请求参数:" + reqParams);
        //获取数据
        Map<String, Object> contentMap = ruyiService.getContentByApi(reqParams, reqType, resType);

        log.info("pull 到的data" + JsonUtil.getString(contentMap));
        if (contentMap.get("code").toString().equals("200")) {
            //todo 校验拉取数据结果,数据完整性校验
            msg.setPullRes(true);
        } else {
            //重试
            log.info("========获取接口数据异常，启动重试================");
            contentMap = retryPull(reqParams, reqType, resType);
        }


//       数据获取成功,推送数据
        if (msg.getPullRes()) {
            //根据模板和获取到的数据，拼接content
            String body = genPushJsonStr(jobVO, contentMap);
            //推送消息
            String webhook = jobVO.getChannelRobotWebhook();
            Boolean pushRes = pushMsg(webhook, body);
            log.info("推送结果:" + pushRes);
            if (pushRes) {
                //更新消息推送结果
                msg.setPushRes(true);
                //推送成功,更新任务状态，更新上次执行时间
                job.setExecStatus(TaskExecStatusEnum.SUCCESS.getCode());
                job.setLastExecTime(LocalDateTime.now());
                log.info("推送消息成功");

            } else {
                log.warn("推送消息异常，启动重试");
                //重试
                retryPush(webhook, body);
            }
            if (!msg.getPushRes()) {
                //重试之后仍push失败
                job.setExecStatus(TaskExecStatusEnum.FAILURES.getCode());
                log.error("重试推送 失败。。。。");
            } else {
                job.setExecStatus(TaskExecStatusEnum.SUCCESS.getCode());
            }
        } else {
            //重试之后，仍pull数据失败
            log.error("重试Pull 失败。。。。");
            job.setExecStatus(TaskExecStatusEnum.FAILURES.getCode());
        }
        //更新任务状态
        scheduleJobService.update(job);
        //更新消息状态
        msgService.update(msg);
    }

    /**
     * @param
     * @return
     * @todo 解析模板中的参数
     * @date 20/06/12 17:23
     */
    private Map<String, Object> parseTemplateParams(String templateRobotPushTemplate) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> templateMp = (Map<String, Object>) JsonUtil.getObj(templateRobotPushTemplate);
//        解析请求参数
        Map<String, Object> reqParmaMp = new HashMap<>();
        reqParmaMp.put("apiId", templateMp.get("apiId").toString());
        reqParmaMp.put("tableName", templateMp.get("tableName"));
        reqParmaMp.put("datasourceName", templateMp.get("datasourceName").toString());
        reqParmaMp.put("queryType", "normal");
//        入参list
        List<Map<String, Object>> entryList = new ArrayList<>();
        //获取时间相关参数
        if (null != templateMp.get("timeParameter")) {
            Map<String, Object> entryParamTime = new HashMap<>();
            Map<String, Object> timeParameter = (Map<String, Object>) templateMp.get("timeParameter");
            entryParamTime.put("columnName", timeParameter.get("columnName").toString());
            //条件表达式，根据起止值，得到日期
            Map<String, String> dateStr = DateUtil.getDateRangeByCurrentTime(true, Integer.valueOf(timeParameter.get("startValue").toString()), Integer.valueOf(timeParameter.get("endValue").toString()));
            entryParamTime.put("conditionType", "condition_equals");
            entryParamTime.put("startValue", dateStr.get("start"));
            entryParamTime.put("endValue", dateStr.get("end"));
            entryList.add(entryParamTime);
        }
        //todo 测试公司
        Map<String, Object> companyMp = new HashMap<>();
        companyMp.put("columnName", "company_id");
        companyMp.put("conditionType", "equals");
        companyMp.put("defaultValue", 91427);
        entryList.add(companyMp);
        reqParmaMp.put("enterParam", entryList);

//        出参list
        List<Map<String, Object>> outList = new ArrayList<>();
        List<Map<String, Object>> groupList = (List<Map<String, Object>>) templateMp.get("merticsContent");
        for (Map<String, Object> group : groupList) {
            List<Map<String, Object>> metaList = (List<Map<String, Object>>) group.get("metricsList");
            for (Map<String, Object> meta : metaList) {
                Map<String, Object> metrics = new HashMap<>();
                metrics.put("columnName", meta.get("meta"));
                outList.add(metrics);
            }
        }
        reqParmaMp.put("outParam", outList);
        res.put("reqParams", reqParmaMp);
//        解析请求类型
        res.put("reqType", templateMp.get("requestType"));
//        解析返回类型
        res.put("resType", templateMp.get("responseType"));
        return res;
    }


    /**
     * @param
     * @return
     * @todo 根据用户选择的指标，拼接推送消息的MarkDown格式的Json串
     * @date 20/05/26 14:20
     */
    private Map getMarkDownContent(Map data, String templateRobotPushTemplate, String msgTitle) {
        log.info("拼接markdown格式的消息内容");
        List<Map<String, Object>> lmData = (List<Map<String, Object>>) data.get("metricsData");
        StringBuilder sb = new StringBuilder();
        Map mp = new HashMap();
        //解析用户配置的模板为json字符串
        Map<String, Object> obj = (Map) JsonUtil.getObj(templateRobotPushTemplate);
        List<Map<String, Object>> contentMapList = (List<Map<String, Object>>) obj.get("merticsContent");

        //处理消息标题，替换变量为数字
        String title = msgTitle;
        if (null != obj.get("timeParameter")) {
            Map<String, Object> timeParameter = (Map<String, Object>) obj.get("timeParameter");
            Map<String, String> ft = DateUtil.getDateRangeByCurrentTime(true, Integer.valueOf(timeParameter.get("startValue").toString()), Integer.valueOf(timeParameter.get("endValue").toString()));
            title = TemplateUtil.replaceVar(msgTitle, DateUtil.getFromToMp(ft.get("start"), ft.get("end")));
        } else {
            //不设置日期参数，则选取当前时间，生成用户变量map
            String from = DateUtil.formatDate(new Date(), DateUtil.DT_FORMAT);
            title = TemplateUtil.replaceVar(msgTitle, DateUtil.getFromToMp(from, from));
        }
        log.info("消息标题" + title);
        //解析模板中二级标题+指标
        int subTitlelen = contentMapList.size();

//        拼装json串
        //title 昨日
        sb.append("**" + title + "**");
        //循环拼接内容
        //二级标题，按id排序
        JsonUtil.sortListJsonByKey(contentMapList, "id");
        //遍历指标数据list
        int dataSize = lmData.size();
        for (int k = 0; k < dataSize; k++) {
            Map<String, Object> metricsData = lmData.get(k);
            //遍历模板中指标，填充数据
            for (int i = 0; i < subTitlelen; i++) {
                Map<String, Object> contentMap = contentMapList.get(i);
                String subTitle = contentMap.get("subtitle").toString();

                //判断指标是否自定义sort，没定义则按 id 排序
                List<Map<String, Object>> metricsMapList = (List<Map<String, Object>>) contentMap.get("metricsList");
                int metricCnt = metricsMapList.size();
                if (metricCnt > 1) {
                    Boolean sortFlag = false;
                    for (Map<String, Object> metrics : metricsMapList) {
                        if ("".equals(metrics.get("sort").toString().trim())) {
                            break;
                        }
                        sortFlag = true; //根据sort排序指标
                    }
                    if (!sortFlag) {
                        JsonUtil.sortListJsonByKey(metricsMapList, "id");
                    } else {
                        JsonUtil.sortListJsonByKey(metricsMapList, "sort");
                    }
                }
                sb.append("\n\n");
                sb.append("**" + subTitle + "**");
                for (int j = 0; j < metricCnt; j++) {

                    Map<String, Object> metricsMap = metricsMapList.get(j);
                    sb.append("\n");
                    //获取指标名或别名，如果没指定别名，则使用metaName
                    String metricsName = metricsMap.get("metaName").toString();
                    String alias = metricsMap.get("alias").toString().trim();
                    if (!"".equals(alias)) {
                        metricsName = alias;
                    }
                    String metricsValue = metricsData.get(metricsMap.get("meta").toString()).toString();
                    sb.append(">" + metricsName + ": <font color=\"info\">" + metricsValue + "</font>");
                }
            }
            //APi获取到多条指标数据，则-------分割
            if (k != dataSize - 1) {
                sb.append("\n\n");
                sb.append("**—————————————**");
            }
        }

        String content = sb.toString();
        //消息内容长度判断
        String maxLen = PropertiesUtil.getProperty("channel.webhook.maxLength");
        try {
            int len = content.getBytes("UTF-8").length;
            if(len>Integer.valueOf(maxLen)){
                content="推送消息内容，超过最大限制"+maxLen+"KB";
            }
        } catch (UnsupportedEncodingException e) {
            log.error("提示: 推送消息内容的长度超过最大限制"+maxLen+"KB");
            e.printStackTrace();
        }
        mp.put("content", content);
        //更新消息内容
        msg.setContent(content);
        return mp;
    }

    /**
     * @param
     * @return 推送模板id：1 text文本 2 markdown 3 图片 4 图文消息
     * @todo 根据用户选择的推送消息类型，生成各类型模板的内容json串
     * @date 20/05/26 14:19
     */
    private String genPushJsonStr(ScheduleJobVO jobVO, Map data) {
        log.info("根据消息推送类型，拼接请求体...");
        Integer type = jobVO.getTemplateRobotPushType();
        Map mp = new HashMap<>();
        if (type == WebhookTemplateTypeEnum.TEXT.getCode()) {
            //todo 文本

        } else if (WebhookTemplateTypeEnum.MARKDOWN.getCode() == type) {
            //markdown
            mp.put("msgtype", "markdown");
            mp.put("markdown", getMarkDownContent(data, jobVO.getTemplateRobotPushTemplate(), jobVO.getSubTitle()));
        } else if (WebhookTemplateTypeEnum.IMAGE.getCode() == type) {
            //todo 图片

        } else if (WebhookTemplateTypeEnum.NEWS.getCode() == type) {
            //todo 图文
        }
        return JsonUtil.getString(mp);
    }

    /**
     * @param
     * @return
     * @todo 生成消息对象
     * @date 20/05/28 9:56
     */
    private Msg createMsg(String jobId) {
        Msg msg = new Msg();
        msg.setJobId(Integer.valueOf(jobId));
        msg.setContent("");
        msg.setGenTime(LocalDateTime.now());
        return msgService.getMsgById(msgService.add(msg));
    }

    /**
     * @param
     * @return
     * @todo 推送消息
     * @date 20/05/28 13:39
     */
    public Boolean pushMsg(String webhook, String body) {
        Response response = apiDao.syncSend(true, webhook, RequestType.POST, body);
        try {
            if (200 == response.code()) {
                String res = response.body().string();
                Map mp = (Map) JsonUtil.getObj(res);
                Integer errcode = (Integer) mp.get("errcode");
                if (errcode == 0) {
                    msg.setPushRes(true);
                    return true;
                }
            } else {
                log.error("微信webhook服务器响应异常");
            }
        } catch (IOException e) {
            log.error("推送数据异常:" + e.getMessage());
        } finally {
            response.close();
        }

        msg.setPushRes(false);
        return false;
    }


    /**
     * @param
     * @return
     * @todo 立即重试push
     * @date 20/05/28 14:30
     */
    public void retryPush(String webhook, String body) {
        Integer interval = Integer.valueOf(PropertiesUtil.getProperty("retry.internal"));
        Integer count = Integer.valueOf(PropertiesUtil.getProperty("retry.count"));
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(interval * 1000);
                Boolean res = pushMsg(webhook, body);
                if (res) {
                    log.info("推送消息成功");
                    msg.setPushRes(true);
                    break;
                }
            } catch (InterruptedException e) {
                log.error("重试发送异常:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * @param
     * @return
     * @todo 立即重试拉取数据
     * @date 20/05/28 15:35
     */
    public Map retryPull(String params, String reqType, String resType) {
        Map map = new HashMap();
        Integer interval = Integer.valueOf(PropertiesUtil.getProperty("retry.internal"));
        Integer count = Integer.valueOf(PropertiesUtil.getProperty("retry.count"));
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(interval * 1000);
                map = ruyiService.getContentByApi(params, reqType, resType);
                if (map.get("code").toString().equals("200")) {
                    msg.setPushRes(true);
                    break;
                }
            } catch (InterruptedException e) {
                msg.setPushRes(false);
                log.info("重试发送异常:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }
}
