package com.enn.noticesystem.plugin.quartz;

import com.enn.noticesystem.service.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//CommandLineRunner 在springBoot启动时，会调用
@Component
public class InitJobSchedule implements CommandLineRunner {

    @Autowired
    private QuartzService taskService;

    /**
     * 任务调度开始
     * @param strings
     * @throws Exception
     */
    @Override
    public void run(String... strings) throws Exception {
        System.out.println("任务调度开始==============任务调度开始");
        taskService.timingTask();
        System.out.println("任务调度结束==============任务调度结束");
    }
}
