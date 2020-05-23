package com.enn.noticesystem.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enn.noticesystem.domain.MsgTemplate;
import com.enn.noticesystem.domain.PushChannel;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.MsgTemplateService;
import com.enn.noticesystem.service.PushChannelService;
import com.enn.noticesystem.service.RuyiService;
import com.enn.noticesystem.service.ScheduleJobService;
import com.enn.noticesystem.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @author liyanfei
 *  @date 20/05/18 17:43
 *  @description 定时任务测试控制器
 *
 */
@RestController
@RequestMapping("job")
@Slf4j
public class ScheduleJobController {
    private HashMap<Object ,Object> res = new HashMap<>();

    @Autowired
    private ScheduleJobService scheduleJobService;
    @Autowired
    RuyiService ruyiService;
    @Autowired
    PushChannelService pushChannelService;
    @Autowired
    MsgTemplateService msgTemplateService;


    //获取请求的头信息
    @Autowired
    HttpServletRequest httpServletRequest;

    @PostMapping("addV/type/{type}")
    public String addV(@PathVariable("type") String type,@RequestBody Map<String,String> reqMap){
        res.clear();
        //准备添加页面需要的数据
        if(type.equals("1")){
            //机器人
            //获取用户id
            String userId="1";
            List<PushChannel> pushChannels = pushChannelService.listChannelsByType(userId, type);
            List<MsgTemplate> msgTemplates = msgTemplateService.listTemplatesByType(userId, type);
            res.put("pushChannels", pushChannels);
            res.put("msgTemplates", msgTemplates);

        } else if(type.equals("2")){

        }else if(type.equals("3")){

        }else if(type.equals("4")){

        }


        return JsonUtil.getString(res);
    }

    @PostMapping("add")
    public String add(@RequestBody ScheduleJob scheduleJob) {
        //任务格式校验

        //获取用户id
        scheduleJob.setCreatorId(1);

        Integer resAdd = scheduleJobService.add(scheduleJob);
        res.put("res", resAdd);
        return JsonUtil.getString(res);
    }

    @PostMapping("update")
    public String update(@RequestBody ScheduleJob scheduleJob){
        Boolean resUpdate = scheduleJobService.update(scheduleJob);

        res.clear();
        res.put("res", resUpdate);
        return JsonUtil.getString(res);
    }

    @GetMapping("del/{id}")
    public String delete(@PathVariable("id") String id) {

        Boolean resDel = scheduleJobService.delete(Integer.valueOf(id));
        res.clear();
        res.put("res", resDel);
        return JsonUtil.getString(res);
    }

    @GetMapping("name/{name}")
    public String getJobsByName(@PathVariable("name")String name){

        //获取用户名
        String userId = "1";
        List<ScheduleJob> scheduleJobs = scheduleJobService.listScheduleJobsByName(userId, name);
        res.clear();
        res.put("length", scheduleJobs.size());
        res.put("content", scheduleJobs);
        return JsonUtil.getString(res);
    }

    @GetMapping("p/{pageNo}/s/{size}")
    public String getJobsByPage(@PathVariable("pageNo")String pageNo,
                                    @PathVariable( "size") String size){
        //获取用户id
        String userId = "1";

        Page<ScheduleJob> scheduleJobPage = new Page<>(Long.valueOf(pageNo), Long.valueOf(size));
        IPage<ScheduleJob> page = scheduleJobService.listSchedulesJobsByPage(scheduleJobPage,userId);
        List<ScheduleJob> records = page.getRecords();
        res.clear();
        res.put("countTotal", scheduleJobService.calRecordsByType(userId));
        res.put("length", records.size());
        res.put("content", records);
        return JsonUtil.getString(res);
    }

    @GetMapping("id/{id}")
    public String getJobById(@PathVariable("id") String id){
        log.info("获取id="+id+"的任务详细信息");
        log.info("请求信息"+httpServletRequest.getRequestURI()+"||"+httpServletRequest.getHeader("test"));
        ScheduleJobVO scheduleJobById = scheduleJobService.getScheduleJobById(id);
        res.clear();
        res.put("content", scheduleJobById);
        return JsonUtil.getString(res);
    }
//    任务执行相关
    @GetMapping("start/{id}")
    public String start(@PathVariable("id") Integer id) {

        return "启动定时任务成功";
    }

    @GetMapping("pause/{id}")
    public String pause(@PathVariable("id") Integer id) {

        return "暂停定时任务成功";
    }

    @GetMapping("startAllJob")
    public String startAllJob() {

        return "启动所有定时任务成功";
    }

    @GetMapping("pauseAllJob")
    public String pauseAllJob() {

        return "暂停所有定时任务成功";
    }
}

