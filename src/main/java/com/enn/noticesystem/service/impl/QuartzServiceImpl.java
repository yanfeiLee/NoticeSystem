package com.enn.noticesystem.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enn.noticesystem.constant.JobOperateEnum;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.plugin.quartz.QuartzFactory;
import com.enn.noticesystem.service.QuartzService;
import com.enn.noticesystem.service.ScheduleJobService;
import com.enn.noticesystem.util.CronUtil;
import com.enn.noticesystem.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


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
        //启动status=1（运行中）的任务，
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(
                //过滤未启动任务
                lqw -> lqw.ne(ScheduleJob::getStatus, "0")
        );
        List<ScheduleJob> scheduleJobs = jobService.list(scheduleJobLambdaQueryWrapper);
        if (scheduleJobs != null) {
            //如果任务停止，则不启动
            for (ScheduleJob job : scheduleJobs) {
                log.info("初始化定时任务：id=" + job.getId()+"任务状态："+job.getStatus());
                addJob(job);
                if (job.getStatus() == 2) {
                    //将status=2(暂停中)的任务加入调度器，状态置为暂停
                    try {
                        log.info("暂停任务，id="+job.getId());
                        operateJob(JobOperateEnum.PAUSE, job);
                    } catch (SchedulerException e) {
                        log.error("初始化暂停状态任务失败:jobid=" + job.getId());
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("系统启动时，初始化执行的任务完成");
    }

    @Override
    public void addJob(ScheduleJob job) {
        //生成任务key
        String identifyKey = job.getId() + "-" + job.getName();
        try {
            //解析cronExpression
            Map<String,String> mp = (Map<String, String>) JsonUtil.getObj(job.getCronExpression());
            String cronExpression = CronUtil.genCronSingle(mp.get("period"),mp.get("time"),mp.get("repeat"));
            log.info("任务表达式："+cronExpression);
            //创建触发器
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(identifyKey)
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(cronExpression)
                            .withMisfireHandlingInstructionFireAndProceed()// 调度时间点，调度器繁忙没有执行，空闲时立即执行
                    )

                    .startNow()
                    .build();
            //创建任务
            JobDetail jobDetail = JobBuilder.newJob(QuartzFactory.class)
                    .withIdentity(identifyKey)
                    .build();

            //传入调度的数据，在QuartzFactory中需要使用
            jobDetail.getJobDataMap().put("scheduleJob", job);

            //调度作业
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("启动任务失败，任务id=" + identifyKey);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void operateJob(JobOperateEnum jobOperateEnum, ScheduleJob job) throws SchedulerException {
        String jobIdentify = job.getId() + "-" + job.getName();
        JobKey jobKey = new JobKey(jobIdentify);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            log.error("任务异常: " + jobIdentify + "不存在");
        }
        switch (jobOperateEnum) {
            case START:
                log.info("恢复/启动任务:" + jobIdentify);
                scheduler.resumeJob(jobKey);
                job.setStatus(1);
                break;
            case PAUSE:
                log.info("暂停任务:" + jobIdentify);
                scheduler.pauseJob(jobKey);
                //更新job
                job.setStatus(2);
                break;
            case DELETE:
                log.info("删除任务:" + jobIdentify);
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
