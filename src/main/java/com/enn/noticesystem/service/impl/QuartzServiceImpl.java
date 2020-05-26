package com.enn.noticesystem.service.impl;


import com.enn.noticesystem.constant.JobOperateEnum;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.plugin.quartz.QuartzFactory;
import com.enn.noticesystem.service.QuartzService;
import com.enn.noticesystem.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Transactional
@Slf4j
public class QuartzServiceImpl implements QuartzService {

    /**
     * 调度器
     */
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ScheduleJobService jobService;


    @Override
    public void timingTask() {
        //系统重启或首次启动时查询数据库是否存在需要定时的任务
//        List<ScheduleJob> scheduleJobs = jobService.list();
//        if (scheduleJobs != null) {
//            //如果任务停止，则不启动
//            scheduleJobs.forEach(this::addJob);
//        }
        log.info("系统启动时，初始化执行的任务。。。");
    }

    @Override
    public void addJob(ScheduleJob job) {
        //生成任务key
        String jobKey = job.getId() + "-" + job.getName();
        try {
            //创建触发器
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobKey)
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(job.getCronExpression())
                            .withMisfireHandlingInstructionDoNothing() //启动任务时不直接运行，而是等到下一个周期时执行
                    )
                    .startNow()
                    .build();

            //创建任务
            JobDetail jobDetail = JobBuilder.newJob(QuartzFactory.class)
                    .withIdentity(job.getName())
                    .build();

            //传入调度的数据，在QuartzFactory中需要使用
             jobDetail.getJobDataMap().put("scheduleJob", job);

            //调度作业
           scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void operateJob(JobOperateEnum jobOperateEnum,ScheduleJob job) throws SchedulerException {
        String jobIdentify = job.getId() + "-" + job.getName();
        JobKey jobKey = new JobKey(jobIdentify);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            log.info("任务异常: "+jobIdentify+"不存在");
        }
        switch (jobOperateEnum) {
            case START:
                log.info("恢复任务:"+jobIdentify);
                scheduler.resumeJob(jobKey);
                break;
            case PAUSE:
                log.info("暂停任务:"+jobIdentify);
                scheduler.pauseJob(jobKey);
                break;
            case DELETE:
                log.info("删除任务:"+jobIdentify);
                scheduler.deleteJob(jobKey);
                break;
        }
    }

    @Override
    public void startAllJob() throws SchedulerException {
        scheduler.start();
    }

    @Override
    public void pauseAllJob() throws SchedulerException {
        scheduler.standby();
    }
}
