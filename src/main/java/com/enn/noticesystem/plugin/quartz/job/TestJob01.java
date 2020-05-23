package com.enn.noticesystem.plugin.quartz.job;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component("testJob01")
@Transactional
//@DisallowConcurrentExecution  不允许并发执行
public class TestJob01 {

    public void execute() {
        System.out.println("-------------------TestJob01任务执行开始-------------------");
        System.out.println(new Date());
        System.out.println("-------------------TestJob01任务执行结束-------------------");
    }
}
