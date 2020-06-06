package com.enn.noticesystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enn.noticesystem.constant.PushChannelTypeEnum;
import com.enn.noticesystem.domain.PushChannel;
import com.enn.noticesystem.service.PushChannelService;
import com.enn.noticesystem.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 13:03
 * Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("channel")
public class PushChannelController {

    @Autowired
    PushChannelService pushChannelService;

    private static HashMap<Object, Object> res = new HashMap<>();

    @PostMapping("addV/type/{type}")
    public String addV(@PathVariable("type") String type,@RequestBody Map<String,String> reqMap){

        //准备添加页面需要的数据
        if(type.equals(PushChannelTypeEnum.ROBOT.getCode().toString())){
            //机器人
        }
        res.clear();
        res.put("test","test");
        return JsonUtil.getString(res);
    }

    @PostMapping("add")
    public String add(@RequestBody PushChannel pushChannel) {
        //数据校验
        res.clear();
        Integer resAdd = pushChannelService.add(pushChannel);
        if(resAdd != -1){
               res.put("res", true);
               res.put("id", resAdd);
        }else{
            res.put("res", false);
        }
        return JsonUtil.getString(res);
    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@RequestBody PushChannel pushChannel) {
        log.info(pushChannel.toString());

        res.clear();
        res.put("res", pushChannelService.update(pushChannel));
        return JsonUtil.getString(res);
    }

    @PostMapping(value = "del/{id}")
    public String del(@PathVariable("id") String id) {
        res.clear();
        res.put("res", pushChannelService.delete(Integer.valueOf(id)));
        return JsonUtil.getString(res);
    }


    @GetMapping("id/{id}")
    public String getChannelById(@PathVariable("id") String id) {
        log.info("获取pushChannel:" + id);
        PushChannel pushChannel = pushChannelService.getChannelById(id);
        res.clear();
        if(null != pushChannel){
            res.put("res", true);
            pushChannelService.addDesc(pushChannel);
            res.put("content", pushChannel);
        }else{
            res.put("res",false );
            res.put("content", "渠道不存在");
        }
        return JsonUtil.getString(res);
    }


    @GetMapping("type/{type}/name/{name}/p/{pageNo}/s/{size}")
    public String getChannelByName(@PathVariable("type")String type,@PathVariable("name")String name,@PathVariable("pageNo")String pageNo,
                                   @PathVariable( "size") String size) {
        //获取当前登录用户
        String loginUserId = "1";
        log.info("请求name="+name);
        Page<PushChannel> pageFromUser = new Page<>(Long.valueOf(pageNo), Long.valueOf(size));
        IPage<PushChannel> page = pushChannelService.listChannelsByName(loginUserId, type, name, pageFromUser);
        for (PushChannel pushChannel : page.getRecords()) {
            pushChannelService.addDesc(pushChannel);
        }
        return JsonUtil.getString(page);
    }

    @GetMapping("type/{type}/p/{pageNo}/s/{size}")
    public String getChannelsByPage(@PathVariable("type")String type,@PathVariable("pageNo")String pageNo,
                                               @PathVariable( "size") String size){
        //获取用户id
        String userId = "1";
        Page<PushChannel> pushChannelPage = new Page<>(Long.valueOf(pageNo), Long.valueOf(size));
        IPage<PushChannel> page = pushChannelService.listPagesByType(pushChannelPage,userId,type);
        for (PushChannel pushChannel : page.getRecords()) {
            pushChannelService.addDesc(pushChannel);
        }
        return JsonUtil.getString(page);
    }

}
