package com.enn.noticesystem.controller;

import com.alibaba.fastjson.JSON;
import com.enn.noticesystem.domain.ScheduleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/18 14:31
 * Version: 1.0
 */
@RestController
@Slf4j
public class testController {

    @RequestMapping("/test")
    public String test() {
        log.info(" 用户请求 - hello world");
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setName("test job");
        String res = JSON.toJSONString(scheduleJob);
        return res;
    }


}
