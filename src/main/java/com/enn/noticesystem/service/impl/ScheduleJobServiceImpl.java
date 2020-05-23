package com.enn.noticesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enn.noticesystem.constant.JobOperateEnum;
import com.enn.noticesystem.dao.mapper.ScheduleJobMapper;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.QuartzService;
import com.enn.noticesystem.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        log.info("添加调度任务："+job.getName());
        //此处省去数据验证
        boolean res = this.save(job);

//         创建任务，立即执行

//        //加入job
//        try {
//            quartzService.addJob(job);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if(res){
            return job.getId();
        }else{
            return -1;
        }
    }

    @Override
    public Boolean update(ScheduleJob job) {
        log.info("更新调度任务："+job.getName());
        boolean res = this.updateById(job);
        return res;
    }

    @Override
    public void start(int id) {
        //此处省去数据验证
        ScheduleJob job = this.getById(id);
        job.setStatus(1);
        this.updateById(job);

        //执行job
        try {
            quartzService.operateJob(JobOperateEnum.START, job);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause(int id) {
        //此处省去数据验证
        ScheduleJob job = this.getById(id);
        job.setStatus(2);
        this.updateById(job);

        //执行job
        try {
            quartzService.operateJob(JobOperateEnum.PAUSE, job);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean delete(int id) {
        //此处省去数据验证
        ScheduleJob job = this.getById(id);

        //任务暂停后，才允许删除

        //执行job
        try {
            quartzService.operateJob(JobOperateEnum.DELETE, job);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        boolean res = this.removeById(id);
        return  res;
    }

    @Override
    public List<ScheduleJob> listScheduleJobsByName(String userId, String name) {
        log.info("查询ScheduleJob:"+name);
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw->lqw.eq(ScheduleJob::getCreatorId ,userId ).eq(ScheduleJob::getName ,name ));
        List<ScheduleJob> list = this.list(scheduleJobLambdaQueryWrapper);
        return list;
    }

    @Override
    public Integer calRecordsByType(String userId) {
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw->lqw.eq(ScheduleJob::getCreatorId , userId));
        int count = this.count(scheduleJobLambdaQueryWrapper);
        return count;
    }

    @Override
    public IPage<ScheduleJob> listSchedulesJobsByPage(IPage<ScheduleJob> page, String userId) {
        log.info("获取用id="+userId+" job的分页信息");
        LambdaQueryWrapper<ScheduleJob> scheduleJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleJobLambdaQueryWrapper.and(lqw->lqw.eq(ScheduleJob::getCreatorId , userId));
        IPage<ScheduleJob> res = this.page(page, scheduleJobLambdaQueryWrapper);
        return res;
    }

    @Override
    public ScheduleJobVO getScheduleJobById(String id) {
        log.info("获取id="+id+"的job的详细信息");
        ScheduleJobVO scheduleJobDetail = this.getBaseMapper().getScheduleJobDetail(id);
        return scheduleJobDetail;
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
