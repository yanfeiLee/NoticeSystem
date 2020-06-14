package com.enn.noticesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enn.noticesystem.constant.*;
import com.enn.noticesystem.dao.mapper.ScheduleJobMapper;
import com.enn.noticesystem.domain.MsgTemplate;
import com.enn.noticesystem.domain.PushChannel;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.MsgTemplateService;
import com.enn.noticesystem.service.PushChannelService;
import com.enn.noticesystem.service.QuartzService;
import com.enn.noticesystem.service.ScheduleJobService;
import com.enn.noticesystem.service.job.WebhookJob;
import com.enn.noticesystem.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    @Autowired
    private PushChannelService pushChannelService;
    @Autowired
    private MsgTemplateService msgTemplateService;

    private LambdaQueryWrapper<ScheduleJob> filterJobByUserId(String userId) {
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw ->
                lqw.eq(ScheduleJob::getCreatorId, Integer.valueOf(userId))
        ).orderByDesc(ScheduleJob::getCreatedTime);
        return scheduleJobLambdaQueryWrapper;
    }

    private LambdaQueryWrapper<ScheduleJob> filterJobByUserIdAndStatus(String userId, String status) {
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw -> lqw.eq(ScheduleJob::getCreatorId, Integer.valueOf(userId))
                .eq(ScheduleJob::getStatus, Integer.valueOf(status)));
        return scheduleJobLambdaQueryWrapper;
    }


    @Override
    public Map<String, Object> addV() {
        Map<String, Object> res = new HashMap<>();
        List<Map<String, Object>> pushTimeTypeList = new ArrayList<>();
        Map<String, Object> imm = new HashMap<>();
        imm.put("code", PushTimeTypeEnum.IMMEDIATELY.getCode());
        imm.put("desc", PushTimeTypeEnum.IMMEDIATELY.getDesc());
        pushTimeTypeList.add(imm);
        Map<String, Object> peri = new HashMap<>();
        peri.put("code", PushTimeTypeEnum.PERIODIC.getCode());
        peri.put("desc", PushTimeTypeEnum.PERIODIC.getDesc());
        pushTimeTypeList.add(peri);
        res.put("pushTimeType", pushTimeTypeList);

        List<Map<String, Object>> pushChannelTypeList = new ArrayList<>();
        Map<String, Object> robot = new HashMap<>();
        robot.put("code", PushChannelTypeEnum.ROBOT.getCode());
        robot.put("desc", PushChannelTypeEnum.ROBOT.getDesc());
        pushChannelTypeList.add(robot);
        Map<String, Object> msg = new HashMap<>();
        msg.put("code", PushChannelTypeEnum.MESSAGE.getCode());
        msg.put("desc", PushChannelTypeEnum.MESSAGE.getDesc());
        pushChannelTypeList.add(msg);
        Map<String, Object> siteMsg = new HashMap<>();
        siteMsg.put("code", PushChannelTypeEnum.SITEMSG.getCode());
        siteMsg.put("desc", PushChannelTypeEnum.SITEMSG.getDesc());
        pushChannelTypeList.add(siteMsg);
        Map<String, Object> email = new HashMap<>();
        email.put("code", PushChannelTypeEnum.EMAIL.getCode());
        email.put("desc", PushChannelTypeEnum.EMAIL.getDesc());
        pushChannelTypeList.add(email);
        res.put("pushChannelType", pushChannelTypeList);

        return res;
    }

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
        log.info("更新调度任务：" + job.getId());
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
                //判断渠道模板，是否开启
                PushChannel channel = pushChannelService.getChannelById(String.valueOf(job.getPushChannelId()));
                MsgTemplate template = msgTemplateService.getTemplateById(String.valueOf(job.getMsgTemplateId()));
                if(channel.getStatus() == TemplateChannelStatusEnum.CLOSE.getCode()){
                    info="渠道【"+channel.getName()+"】未开启，请先开启该推送渠道";
                }else if(template.getStatus() == TemplateChannelStatusEnum.CLOSE.getCode()){
                    info="模板【"+template.getName()+"】未开启，请先开启该消息模板";
                }else{
                    //判断任务 推送时间类型
                    if(job.getPushTimeType()==PushTimeTypeEnum.IMMEDIATELY.getCode()){
                        //启动推送
                        Object object = SpringUtil.getBean(job.getServiceName());
                        try {
                            //利用反射执行对应方法
                            Method method = object.getClass().getMethod(job.getMethodName(),String.class);
                            method.invoke(object,job.getId().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        job.setStatus(TaskStatusEnum.RUNNING.getCode());
                        this.update(job);
                    }else{
                        //判断首次启动还是恢复任务
                        if (TaskStatusEnum.WATIING.getCode() == job.getStatus()) {
                            quartzService.addJob(job);
                            //修改任务状态
                            job.setStatus(TaskStatusEnum.RUNNING.getCode());
                            this.update(job);
                            res = true;
                            info = "启动成功";
                        } else if (TaskStatusEnum.RUNNING.getCode() == job.getStatus()) {
                            //任务已启动
                            res = false;
                            info = "重复启动任务";
                        } else {
                            quartzService.operateJob(JobOperateEnum.START, job);
                            //修改任务状态
                            job.setStatus(TaskStatusEnum.RUNNING.getCode());
                            this.update(job);
                            res = true;
                            info = "恢复任务成功";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("启动任务异常：" + e.getMessage());
            info = "启动任务失败";
        } finally {
            mp.put("res", res);
            mp.put("info", info);
            mp.put("content", job);
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
                if (TaskStatusEnum.PAUSE.getCode() == job.getStatus()) {
                    info = "任务已暂停,重复暂停";
                } else {
                    quartzService.operateJob(JobOperateEnum.PAUSE, job);
                    //修改任务状态
                    job.setStatus(TaskStatusEnum.PAUSE.getCode());
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
            mp.put("content", job);
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
            addJobDesc(job);
            return job;
        } else {
            log.error("任务id=" + id + "的任务不存在！");
        }
        return null;
    }

    @Override
    public IPage<ScheduleJob> listScheduleJobsByName(String userId, String name, Page<ScheduleJob> page) {
        log.info("查询ScheduleJob:" + name);
        LambdaQueryWrapper<ScheduleJob> allWrapper = filterJobByUserId(userId);
        allWrapper.and(lqw -> lqw.eq(ScheduleJob::getCreatorId, userId).like(ScheduleJob::getName, name));
        IPage<ScheduleJob> res = this.page(page, allWrapper);
        return res;
    }

    @Override
    public List<ScheduleJob> listScheduleJobsByChannelId(String userId, String channelId, String jobStatus) {
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = filterJobByUserIdAndStatus(userId, jobStatus);
        LambdaQueryWrapper<ScheduleJob> allWrapper = scheduleJobLambdaQueryWrapper.and(lqw -> lqw.eq(ScheduleJob::getPushChannelId, Integer.valueOf(channelId)));
        List<ScheduleJob> list = this.list(allWrapper);
        return list;
    }

    @Override
    public List<ScheduleJob> listScheduleJobsByTemplateId(String userId, String templateId, String jobStatus) {
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = filterJobByUserIdAndStatus(userId, jobStatus);
        LambdaQueryWrapper<ScheduleJob> allWrapper = scheduleJobLambdaQueryWrapper.and(lqw -> lqw.eq(ScheduleJob::getMsgTemplateId, Integer.valueOf(templateId)));
        List<ScheduleJob> list = this.list(allWrapper);
        return list;
    }


    @Override
    public Integer calRecordsByType(String userId) {
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = filterJobByUserId(userId);
        int count = this.count(scheduleJobLambdaQueryWrapper);
        return count;
    }

    @Override
    public IPage<ScheduleJob> listSchedulesJobsByPage(IPage<ScheduleJob> page, String userId) {
        log.info("获取用id=" + userId + " job的分页信息");
        LambdaQueryWrapper<ScheduleJob> allWrapper = filterJobByUserId(userId);
        allWrapper.and(lqw -> lqw.eq(ScheduleJob::getCreatorId, userId));
        IPage<ScheduleJob> res = this.page(page, allWrapper);
        return res;
    }

    @Override
    public ScheduleJobVO getScheduleJobVOById(Integer id) {
        log.info("获取id=" + id + "的job的详细信息");
        ScheduleJobVO scheduleJobDetail = this.getBaseMapper().getScheduleJobDetail(id);
        if (null != scheduleJobDetail) {
            addJobVoDesc(scheduleJobDetail);
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
    public void addJobDesc(ScheduleJob job) {
        job.setStatusDesc(TaskStatusEnum.getDescByCode(job.getStatus()));
        job.setExecStatusDesc(TaskExecStatusEnum.getDescByCode(job.getExecStatus()));
        job.setPushTimeTypeDesc(PushTimeTypeEnum.getDescByCode(job.getPushTimeType()));
        job.setPushChannelTypeDesc(PushChannelTypeEnum.getDescByCode(job.getPushChannelType()));
    }

    @Override
    public void addJobVoDesc(ScheduleJobVO job) {
        //通用信息
        job.setStatusDesc(TaskStatusEnum.getDescByCode(job.getStatus()));
        job.setExecStatusDesc(TaskExecStatusEnum.getDescByCode(job.getExecStatus()));
        job.setPushTimeTypeDesc(PushTimeTypeEnum.getDescByCode(job.getPushTimeType()));
        job.setPushChannelTypeDesc(PushChannelTypeEnum.getDescByCode(job.getPushChannelType()));
        //特地渠道推送任务的信息
        if (job.getPushChannelType() == PushChannelTypeEnum.ROBOT.getCode()) {
            job.setTemplateRobotPushTypeDesc(WebhookTemplateTypeEnum.getDescByCode(job.getTemplateRobotPushType()));
        }
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
