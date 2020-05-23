package com.enn.noticesystem.plugin.quartz.job;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component("testJob02")
@Transactional
@DisallowConcurrentExecution
public class TestJob02 {

    public void execute() throws InterruptedException {
        System.out.println("-------------------TestJob02任务执行开始-------------------");
        System.out.println(LocalDateTime.now());
        System.out.println(System.currentTimeMillis());
        System.out.println("-------------------TestJob02任务执行结束-------------------");
    }
}