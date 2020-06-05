package com.enn.noticesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enn.noticesystem.constant.JobOperateEnum;
import com.enn.noticesystem.dao.mapper.ScheduleJobMapper;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.QuartzService;
import com.enn.noticesystem.service.ScheduleJobService;
import com.enn.noticesystem.service.job.WebhookJob;
import com.enn.noticesystem.util.CronUtil;
import com.enn.noticesystem.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.security.interfaces.RSAKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lanjerry
 * @since 2019-01-28
 */
@Service
@Transactional
@Slf4j
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Autowired
    private QuartzService quartzService;

    @Override
    public Integer add(ScheduleJob job) {
        log.info("创建调度任务：" + job.getName());

        //此处省去数据验证

        boolean res = this.save(job);

//         创建任务，立即执行

//        //加入job
//        try {
//            quartzService.addJob(job);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (res) {
            return job.getId();
        } else {
            return -1;
        }
    }

    @Override
    public Boolean update(ScheduleJob job) {
        log.info("更新调度任务：" + job.getName());
        boolean res = this.updateById(job);
        return res;
    }

    @Override
    public Map<String, Object> start(int id) {
        Map<String, Object> mp = new HashMap<>();
        Boolean res = false;
        String info = "任务不存在";
        ScheduleJob job = this.getScheduleJobById(id);

        try {
            if (null != job) {
                //判断首次启动还是恢复任务
                if (0 == job.getStatus()) {
                    quartzService.addJob(job);
                    //修改任务状态
                    job.setStatus(1);
                    this.update(job);
                    res = true;
                    info = "启动成功";
                } else if (1 == job.getStatus()) {
                    //任务已启动
                    res = false;
                    info = "重复启动任务";
                } else {
                    quartzService.operateJob(JobOperateEnum.START, job);
                    //修改任务状态
                    job.setStatus(1);
                    this.update(job);
                    res = true;
                    info = "恢复任务成功";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("启动任务异常：" + e.getMessage());
            info = "启动任务失败";
        } finally {
            mp.put("res", res);
            mp.put("info", info);
            return mp;
        }
    }

    @Override
    public Map<String, Object> pause(int id) {
        Map<String, Object> mp = new HashMap<>();
        Boolean res = false;
        String info = "任务不存在";
        ScheduleJob job = this.getScheduleJobById(id);
        try {
            if (null != job) {

                if (2 == job.getStatus()) {
                    info = "任务已暂停,重复暂停";
                } else {
                    quartzService.operateJob(JobOperateEnum.PAUSE, job);
                    //修改任务状态
                    job.setStatus(2);
                    this.update(job);
                    res = true;
                    info = "暂停成功";
                }
            } else {
                log.error("id=" + id + " 的任务不存在");
            }
        } catch (SchedulerException e) {
            log.error("任务暂停异常:" + e.getMessage());
            e.printStackTrace();
        } finally {
            mp.put("res", res);
            mp.put("info", info);
            return mp;
        }
    }

    @Override
    public Map<String, Object> delete(int id) {
        Map<String, Object> mp = new HashMap<>();
        Boolean res = false;
        String info = "任务不存在";
        //此处省去数据验证
        ScheduleJob job = this.getScheduleJobById(id);
        //执行job
        try {
            if (null != job) {
                log.info("删除任务 " + job.getId());
                //任务暂停后，才允许删除
                if (1 == job.getStatus()) {
                    info = "暂停任务后，才能删除";
                } else if (0 == job.getStatus()) {
                    info = "任务创建后未启动";
                    res = this.removeById(id);
                } else {
                    quartzService.operateJob(JobOperateEnum.DELETE, job);
                    res = this.removeById(id);
                    info = "删除成功";
                }
            } else {
                log.error("id=" + id + "的任务不存在");
            }
        } catch (SchedulerException e) {
            log.error("删除任务异常：" + e.getMessage());
            info = "删除失败";
            e.printStackTrace();
        } finally {
            mp.put("res", res);
            mp.put("info", info);
            return mp;
        }
    }

    @Override
    public ScheduleJob getScheduleJobById(Integer id) {
        //验证用户信息
        log.info("获取id=" + id + "的job的任务信息");
        ScheduleJob job = this.getById(id);
        if (null != job) {
            return job;
        } else {
            log.error("任务id=" + id + "的任务不存在！");
        }
        return null;
    }

    @Override
    public IPage<ScheduleJob> listScheduleJobsByName(String userId, String name, Page<ScheduleJob> page) {
        log.info("查询ScheduleJob:" + name);
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw -> lqw.eq(ScheduleJob::getCreatorId, userId).eq(ScheduleJob::getName, name));
        IPage<ScheduleJob> res = this.page(page, scheduleJobLambdaQueryWrapper);
        return res;
    }


    @Override
    public Integer calRecordsByType(String userId) {
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw -> lqw.eq(ScheduleJob::getCreatorId, userId));
        int count = this.count(scheduleJobLambdaQueryWrapper);
        return count;
    }

    @Override
    public IPage<ScheduleJob> listSchedulesJobsByPage(IPage<ScheduleJob> page, String userId) {
        log.info("获取用id=" + userId + " job的分页信息");
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw -> lqw.eq(ScheduleJob::getCreatorId, userId));
        IPage<ScheduleJob> res = this.page(page, scheduleJobLambdaQueryWrapper);
        return res;
    }

    @Override
    public ScheduleJobVO getScheduleJobVOById(Integer id) {
        log.info("获取id=" + id + "的job的详细信息");
        ScheduleJobVO scheduleJobDetail = this.getBaseMapper().getScheduleJobDetail(id);
        if (null != scheduleJobDetail) {
            return scheduleJobDetail;
        } else {
            log.error("任务id=" + id + "的任务不存在！");
        }
        return null;
    }

    @Override
    public ScheduleJob setServiceAndMethod(ScheduleJob scheduleJob) {
        int typeId = scheduleJob.getPushChannelType();
        if (1 == typeId) {
            //企业微信机器人
            scheduleJob.setServiceName(WebhookJob.class.getSimpleName());
            scheduleJob.setMethodName("execute");
        } else if (2 == typeId) {
            //短信
        } else if (3 == typeId) {
            //站内信
        } else {
            //邮件
        }

        return scheduleJob;
    }


    @Override
    public void startAllJob() {
        //此处省去数据验证
        ScheduleJob job = new ScheduleJob();
        job.setStatus(1);
        this.update(job, new QueryWrapper<>());

        //执行job
        try {
            quartzService.startAllJob();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseAllJob() {
        //此处省去数据验证
        ScheduleJob job = new ScheduleJob();
        job.setStatus(2);
        this.update(job, new QueryWrapper<>());

        //执行job
        try {
            quartzService.pauseAllJob();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
