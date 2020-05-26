package com.enn.noticesystem.service.job;

import com.enn.noticesystem.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    /**
    * @todo 执行推送任务，并将推送结果写入库
    * @date 20/05/26 14:21
    * @param
    * @return
    *
    */
    public void execute(String id) {
        log.info("执行webHook推送任务。。。");

        //推送消息
        log.info("执行任务id="+id+"的任务,具体信息为:"+scheduleJobService.getScheduleJobVOById(Integer.valueOf(id)).toString());
        //记录消息推送情况到DB

        log.info("任务执行完成。。。");
    }

    /**
    * @todo 根据用户选择的指标，拼接推送消息的Json串
    * @date 20/05/26 14:20
    * @param
    * @return
    *
    */
    private String genJsonStr() {
        return null;
    }

    /**
    * @todo 根据用户选择的推送消息类型，生成各类型的json框架
    * @date 20/05/26 14:19
    * @param
    * @return
    *
    */
    private String genTemplateFrame(String type){
        return null;
    }
}
