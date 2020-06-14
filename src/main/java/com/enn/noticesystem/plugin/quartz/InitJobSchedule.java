package com.enn.noticesystem.plugin.quartz;

import com.enn.noticesystem.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

//CommandLineRunner 在springBoot启动时，会调用
@Component
@Slf4j
public class InitJobSchedule implements CommandLineRunner {

    @Autowired
    private QuartzService quartzService;

    /**
     * 任务调度开始
     * @param strings
     * @throws Exception
     */
    @Override
    public void run(String... strings) throws Exception {
        log.info("==================系统启动，初始化定时任务==================");
        quartzService.timingTask();
        log.info("==============初始化定时任务结束============");
    }
}
