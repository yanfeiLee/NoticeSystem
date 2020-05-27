package com.enn.noticesystem.service.job;

import com.enn.noticesystem.constant.RequestType;
import com.enn.noticesystem.dao.api.ApiDao;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.ScheduleJobService;
import com.enn.noticesystem.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    ApiDao apiDao;

    /**
    * @todo 执行推送任务，并将推送结果写入库
    * @date 20/05/26 14:21
    * @param
    * @return
    * 每个机器人发送的消息不能超过20条/分钟。
    */
    public void execute(String id) {
        log.info("执行webHook推送任务。。。");
        ScheduleJob job = scheduleJobService.getScheduleJobById(Integer.valueOf(id));
        ScheduleJobVO jobVO = scheduleJobService.getScheduleJobVOById(Integer.valueOf(id));
        //推送消息
        log.info("执行任务id="+id+"的任务,具体信息为:"+jobVO);

        String body = genPushJsonStr(jobVO);
        //推送消息
        Response response = apiDao.syncSend(true, jobVO.getChannelRobotWebhook(), RequestType.POST, body);
        int res = response.code();
        log.info("推送结果:"+res);
        if(200 == res){
            //推送成功,更新任务状态，更新上次执行时间
            job.setExecStatus(1);
            job.setLastExecTime(LocalDateTime.now());
            log.info("推送消息成功");
            //记录消息

        }else{
            //推送失败

            log.info("推送消息错误");

            //
        }

        //记录消息推送情况到DB

    }

    /**
    * @todo 根据用户选择的指标，拼接推送消息的MarkDown格式的Json串
    * @date 20/05/26 14:20
    * @param
    * @return
    *
    */
    private Map getMarkDownContent(ScheduleJobVO jobVO) {
        StringBuilder sb = new StringBuilder();
        Map mp = new HashMap();
        //解析用户配置的模板为json字符串
        String templateRobotPushTemplate = jobVO.getTemplateRobotPushTemplate();
        Map obj = (Map) JsonUtil.getObj(templateRobotPushTemplate);

        log.info("模板对象："+obj.toString());
//        根据用户选的主题，获取接口数据
          if(true){
              //获取数据正常
          }else{
              //获取数据异常，数据为null 或 接口异常
              //启动定时器，循环调用接口，获取数据
          }

         int subTitlelen = 3;
         int merticsLen = 4;
//        拼装json串
        //title 昨日
        sb.append("**"+jobVO.getSubTitle()+"**");
        //循环拼接内容
        for (int i = 0; i < subTitlelen; i++) {
            sb.append("\n\n");
            String subTitle="二级标题";
            sb.append("**"+ subTitle+"**");
            for (int j = 0; j < merticsLen; j++) {
                sb.append("\n");
                String m1 = "指标"+(j+1);
                sb.append(">"+m1+": <font color=\"info\">"+(j+1)*23+"</font>");
            }
        }

        mp.put("content",sb.toString());

        return mp;
    }

    /**
    * @todo 根据用户选择的推送消息类型，生成各类型模板的内容json串
    * @date 20/05/26 14:19
    * @param
    * @return
    * 推送模板id：1 text文本 2 markdown 3 图片 4 图文消息
    */
    private String genPushJsonStr(ScheduleJobVO jobVO){
        Integer type = jobVO.getTemplateRobotPushType();
        Map mp = new HashMap<>();
        if(1==type){
            //文本

        }else if(2 == type){
            //markdown
            mp.put("msgtype", "markdown");
            mp.put("markdown", getMarkDownContent(jobVO));
        }else if(3 == type){
            //图片

        }else if(4 == type){
            //图文
        }
        return JsonUtil.getString(mp);
    }
}
