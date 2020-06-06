package com.enn.noticesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;

import java.util.List;
import java.util.Map;

/**
 *  @author liyanfei
 *  @date 20/05/22 9:42
 *  @description 任务调度服务
 *
 */
public interface ScheduleJobService extends IService<ScheduleJob> {
//     任务管理相关
    /**
    * @todo 添加任务界面数据
    * @date 20/06/06 16:45
    * @param
    * @return
    *
    */
    Map<String,Object>  addV();
    /**
    * @todo 新增任务
    * @date 20/05/22 9:43
    * @param job 被调度的任务
    * @return
    *
    */
    Integer add(ScheduleJob job);

    /**
    * @todo 更新任务
    * @date 20/05/22 9:50
    * @param
    * @return
    *
    */
    Boolean update(ScheduleJob job);

    /**
     * @todo 删除定时任务（逻辑删除）
     * @date 20/05/22 9:44
     * @param
     * @return
     *
     */
    Map<String,Object> delete(int id);


    /**
    * @todo 根据jobId获取 job信息（不包含外键相关信息）
    * @date 20/05/26 15:32
    * @param
    * @return
    *
    */
    ScheduleJob getScheduleJobById(Integer id);

    /**
     * @todo 根据用户id、调度任务名，列出调度任务列表
     * @date 20/05/22 9:54
     * @param
     * @return
     *
     */
    IPage<ScheduleJob> listScheduleJobsByName(String userId,String name,Page<ScheduleJob> page);

    /**
     * @todo 计算用户 创建任务的总个数
     * @date 20/05/21 17:53
     * @param 用户id
     * @return
     *
     */
    Integer calRecordsByType(String userId);
    /**s
    * @todo 根据用户id、page信息，进行分页
    * @date 20/05/22 10:54
    * @param
    * @return
    *
    */
    IPage<ScheduleJob> listSchedulesJobsByPage(IPage<ScheduleJob> page,String userId);

    /**
    * @todo 根据id 获取任务详细信息
    * @date 20/05/22 15:53
    * @param
    * @return
    */
    ScheduleJobVO getScheduleJobVOById(Integer id);
    /**
    * @todo 根据job的推送渠道类型，设置执行的service和method
    * @date 20/05/26 14:47
    * @param scheduleJob 任务信息
    * @return
    *
    */
    ScheduleJob setServiceAndMethod(ScheduleJob scheduleJob);

    /**
    * @todo 添加job 字典值得描述
    * @date 20/06/06 18:56
    * @param
    * @return
    *
    */
    void addJobDesc(ScheduleJob job);
    
    /**
    * @todo  添加jobVo描述
    * @date 20/06/06 18:59
    * @param
    * @return 
    * 
    */
    void addJobVoDesc(ScheduleJobVO job);

//    任务执行相关

    /**
     * @todo 启动定时任务
     * @date 20/05/22 9:44
     * @param
     * @return
     *
     */
    Map<String,Object> start(int id);

    /**
    * @todo 暂停定时任务
    * @date 20/05/22 9:44
    * @param
    * @return
    *
    */
    Map<String,Object> pause(int id);

    /**
    * @todo 启动所有定时任务
    * @date 20/05/22 9:45
    * @param
    * @return
    *
    */
    void startAllJob();

    /**
    * @todo 暂停所有定时任务
    * @date 20/05/22 9:45
    * @param
    * @return
    *
    */
    void pauseAllJob();


}
