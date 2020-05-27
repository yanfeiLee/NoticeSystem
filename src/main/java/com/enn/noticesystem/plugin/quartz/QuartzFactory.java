package com.enn.noticesystem.plugin.quartz;

import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.util.SpringUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.Method;

//调度器根据任务和触发时间 执行任务
public class QuartzFactory implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //获取调度数据
        ScheduleJob scheduleJob = (ScheduleJob) jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
//        jobExecutionContext.
        //获取对应的Bean
        Object object = SpringUtil.getBean(scheduleJob.getServiceName());
        try {
            //利用反射执行对应方法
            Method method = object.getClass().getMethod(scheduleJob.getMethodName(),String.class);
            method.invoke(object,scheduleJob.getId().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
