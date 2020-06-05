package com.enn.noticesystem.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enn.noticesystem.domain.MsgTemplate;
import com.enn.noticesystem.domain.PushChannel;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import com.enn.noticesystem.service.*;
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
 * @author liyanfei
 * @date 20/05/18 17:43
 * @description 定时任务测试控制器
 */
@RestController
@RequestMapping("job")
@Slf4j
public class ScheduleJobController {
    private HashMap<Object, Object> res = new HashMap<>();

    @Autowired
    private ScheduleJobService scheduleJobService;
    @Autowired
    RuyiService ruyiService;
    @Autowired
    PushChannelService pushChannelService;
    @Autowired
    MsgTemplateService msgTemplateService;
    @Autowired
    QuartzService quartzService;

    //获取请求的头信息
    @Autowired
    HttpServletRequest httpServletRequest;

    @PostMapping("addV/type/{type}")
    public String addV(@PathVariable("type") String type, @RequestBody Map<String, String> reqMap) {
        res.clear();
        //准备添加页面需要的数据
        if (type.equals("1")) {
//            //机器人
//            //获取用户id
//            String userId = "1";
//            List<PushChannel> pushChannels = pushChannelService.listChannelsByType(userId, type);
//            List<MsgTemplate> msgTemplates = msgTemplateService.listTemplatesByType(userId, type);
//            res.put("pushChannels", pushChannels);
//            res.put("msgTemplates", msgTemplates);

        } else if (type.equals("2")) {

        } else if (type.equals("3")) {

        } else if (type.equals("4")) {

        }


        return JsonUtil.getString(res);
    }

    @PostMapping("add")
    public String add(@RequestBody ScheduleJob scheduleJob) {
        res.clear();
        //1.根据任务类型，设置调度执行时调用的service和method
        scheduleJobService.setServiceAndMethod(scheduleJob);
        //获取用户id
        scheduleJob.setCreatorId(1);

        Integer resAdd = scheduleJobService.add(scheduleJob);

        res.put("res", resAdd);
        return JsonUtil.getString(res);
    }

    @PostMapping("update")
    public String update(@RequestBody ScheduleJob scheduleJob) {
        Boolean resUpdate = scheduleJobService.update(scheduleJob);

        res.clear();
        res.put("res", resUpdate);
        return JsonUtil.getString(res);
    }

    @GetMapping("del/{id}")
    public String delete(@PathVariable("id") String id) {

        Map<String, Object> resDel = scheduleJobService.delete(Integer.valueOf(id));
        return JsonUtil.getString(resDel);
    }

    @GetMapping("name/{name}/p/{pageNo}/s/{size}")
    public String getJobsByName(@PathVariable("name") String name,@PathVariable("pageNo") String pageNo,
                                @PathVariable("size") String size) {
        //获取用户名
        String userId = "1";
        Page<ScheduleJob> scheduleJobPage = new Page<>(Long.valueOf(pageNo), Long.valueOf(size));
        IPage<ScheduleJob> page = scheduleJobService.listScheduleJobsByName(userId, name, scheduleJobPage);
        return JsonUtil.getString(page);
    }

    @GetMapping("p/{pageNo}/s/{size}")
    public String getJobsByPage(@PathVariable("pageNo") String pageNo,
                                @PathVariable("size") String size) {
        res.clear();
        //获取用户id
        String userId = "1";

        Page<ScheduleJob> scheduleJobPage = new Page<>(Long.valueOf(pageNo), Long.valueOf(size));
        IPage<ScheduleJob> page = scheduleJobService.listSchedulesJobsByPage(scheduleJobPage, userId);
        return JsonUtil.getString(page);
    }

    @GetMapping("id/{id}")
    public String getJobById(@PathVariable("id") String id) {
        ScheduleJobVO scheduleJobById = scheduleJobService.getScheduleJobVOById(Integer.valueOf(id));
        res.clear();
        if(null != scheduleJobById){
            res.put("res", true);
            res.put("content", scheduleJobById);
        }else{
            res.put("res", false);
            res.put("content", "任务不存在");
        }
        return JsonUtil.getString(res);
    }

    //   任务执行相关
    @GetMapping("start/{id}")
    public String start(@PathVariable("id") String id) {
        //启动任务
        Map<String, Object> resMp = scheduleJobService.start(Integer.valueOf(id));
        return JsonUtil.getString(resMp);
    }

    @GetMapping("pause/{id}")
    public String pause(@PathVariable("id") String id) {
        Map<String, Object> resPause = scheduleJobService.pause(Integer.valueOf(id));
        return JsonUtil.getString(resPause);
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

