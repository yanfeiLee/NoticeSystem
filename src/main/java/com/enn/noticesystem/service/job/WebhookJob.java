package com.enn.noticesystem.service.job;

import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.dao.api.ApiDao;
import com.enn.noticesystem.domain.Msg;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.MsgService;
import com.enn.noticesystem.service.RuyiService;
import com.enn.noticesystem.service.ScheduleJobService;
import com.enn.noticesystem.util.DateUtil;
import com.enn.noticesystem.util.JsonUtil;
import com.enn.noticesystem.util.PropertiesUtil;
import com.enn.noticesystem.util.TemplateUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //推送消息
        log.info("执行任务id=" + id + "的任务,具体信息为:" + jobVO);

        //生成消息id
        msg = createMsg(id);

        //解析模板中,获取数据的接口，拉取数据
        //todo 解析模板的api地址及指标


        String  params = "{\"id\":\"2\"}";
        //拉取数据
        Map<String, Object> contentMap = ruyiService.getContentByApi(params);


        log.info("拉取结果:" + msg.getPullRes());
        if (contentMap.get("code").toString().equals("200")) {
            //校验拉取数据结果,数据完整性校验
            msg.setPullRes(true);

        }else{
            //重试
            log.info("获取接口数据异常，启动重试");
            contentMap = retryPull(params);

        }


       //数据获取成功
        if(msg.getPullRes()){
// */
//        if (true) {
//            Map resPullMap = null;

            //根据模板和获取到的数据，拼接content
            String body = genPushJsonStr(jobVO, contentMap);
            //推送消息
            String webhook = jobVO.getChannelRobotWebhook();
            Boolean pushRes = pushMsg(webhook, body);
            log.info("推送结果:" + pushRes);
            if (pushRes) {
                //推送成功,更新任务状态，更新上次执行时间
                job.setExecStatus(1);
                job.setLastExecTime(LocalDateTime.now());
                log.info("推送消息成功");
                //更新消息推送结果
                msg.setPushRes(true);
            } else {
                log.warn("推送消息异常，启动重试");
                //重试
                retryPush(webhook, body);
            }
            if (!msg.getPushRes()) {
                //重试之后仍push失败
                log.error("重试推送 失败。。。。");
            }
        } else {
            //重试之后，仍pull数据失败
            log.error("重试Pull 失败。。。。");
        }
    }

    /**
     * @param
     * @return
     * @todo 根据用户选择的指标，拼接推送消息的MarkDown格式的Json串
     * @date 20/05/26 14:20
     */
    private Map getMarkDownContent(Map data, String templateRobotPushTemplate, String msgTitle) {
        log.info("拼接markdown格式的消息内容");
        StringBuilder sb = new StringBuilder();
        Map mp = new HashMap();
        //解析用户配置的模板为json字符串
        Map obj = (Map) JsonUtil.getObj(templateRobotPushTemplate);
        List<Map<String,Object>> contentMapList = (List<Map<String, Object>>) obj.get("merticsContent");

        //处理消息标题，替换变量为数字
        String title = TemplateUtil.replaceVar(msgTitle, DateUtil.getYesterdayDate());

        log.info("模板对象：" + obj.toString());
        //解析模板中二级标题+指标
        int subTitlelen = contentMapList.size();

//        拼装json串
        //title 昨日
        sb.append("**" + title + "**");
        //循环拼接内容
        //二级标题，按id排序
        JsonUtil.sortListJsonById(contentMapList);
        for (int i = 0; i < subTitlelen; i++) {
            Map<String, Object> contentMap = contentMapList.get(i);
            String subTitle = contentMap.get("subtitle").toString();

            //指标按 id 排序
            List<Map<String,Object>> metricsMapList = (List<Map<String, Object>>) contentMap.get("metricsList");
            JsonUtil.sortListJsonById(metricsMapList);


            sb.append("\n\n");
            sb.append("**" + subTitle + "**");
            for (int j = 0; j < metricsMapList.size(); j++) {

                Map<String, Object> metricsMap = metricsMapList.get(j);
                sb.append("\n");
                //获取指标名或别名，如果没指定别名，则使用metaName
                String metricsName = "";
                String alias = metricsMap.get("alias").toString();
                if(!"".equals(alias)){
                    metricsName = alias;
                }else{
                    metricsName = metricsMap.get("metaName").toString();
                }

//                String  metricsData = (String) data.get(metricsMap.get("meta").toString());
                String  metricsData = "10087";

                sb.append(">" + metricsName + ": <font color=\"info\">" + "${"+metricsMap.get("meta")+"}"+ "</font>");
            }
        }
        String content =TemplateUtil.replaceVar( sb.toString(), data);
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
        log.info("根据消息推送类型，拼接请求体");

        Integer type = jobVO.getTemplateRobotPushType();
        Map mp = new HashMap<>();
        if (1 == type) {
            //文本

        } else if (2 == type) {
            //markdown
            mp.put("msgtype", "markdown");
            mp.put("markdown", getMarkDownContent(data, jobVO.getTemplateRobotPushTemplate(), jobVO.getSubTitle()));
        } else if (3 == type) {
            //图片

        } else if (4 == type) {
            //图文
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
        if(200 == response.code()){
            try {
                String res = response.body().string();
                Map mp = (Map) JsonUtil.getObj(res);
                Integer errcode = (Integer) mp.get("errcode");
                if (errcode == 0) {
                    msg.setPushRes(true);
                    return true;
                }
            } catch (IOException e) {
                log.error("推送数据异常:" + e.getMessage());
            }
        }else{
            log.error("微信webhook服务器响应异常");
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
    public Map retryPull(String params) {
        Map map = new HashMap();
        Integer interval = Integer.valueOf(PropertiesUtil.getProperty("retry.internal"));
        Integer count = Integer.valueOf(PropertiesUtil.getProperty("retry.count"));
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(interval * 1000);
                map = ruyiService.getContentByApi(params);
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
