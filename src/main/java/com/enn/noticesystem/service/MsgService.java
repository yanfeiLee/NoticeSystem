package com.enn.noticesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enn.noticesystem.domain.Msg;

import java.util.List;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/27 18:29
 * Version: 1.0
 */
public interface MsgService extends IService<Msg> {

    /**
     * @todo 新增消息
     * @date 20/05/20 11:02
     * @param msg 消息对象
     * @return 插入成功返回id,失败返回-1
     *
     */
    Integer add(Msg msg);

    /**
     * @todo 更新信息
     * @date 20/05/20 11:07
     * @param  msg 消息对象
     * @return 成功true,失败false
     */
    boolean update(Msg msg);

    /**
    * @todo 根据消息 id获取消息对象
    * @date 20/05/28 13:18
    * @param
    * @return
    *
    */
    Msg getMsgById(Integer id);


    /**
    * @todo 根据jobId,获取所有推送的消息
    * @date 20/05/27 18:46
    * @param
    * @return
    *
    */
    List<Msg> listMsgByJobId(Integer jobId);

    /**
    * @todo 根据jobId 及 拉取数据 推送数据的结果获取消息
    * @date 20/05/27 18:48
    * @param
    * @return
    *
    */
    List<Msg> listMsgByRes(Integer jobId,Boolean pullRes,Boolean pushRes);

}
