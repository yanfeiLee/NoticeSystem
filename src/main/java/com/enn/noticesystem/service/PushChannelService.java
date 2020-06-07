package com.enn.noticesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enn.noticesystem.domain.PushChannel;

import java.util.List;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 10:59
 * Version: 1.0
 */
public interface PushChannelService extends IService<PushChannel> {

    /**
    * @todo 新增推送渠道
    * @date 20/05/20 11:02
    * @param pushChannel 渠道对象
    * @return 插入成功返回id,失败返回-1
    *
    */
    Integer add(PushChannel pushChannel);
    /**
    * @todo 添加状态信息描述
    * @date 20/06/06 19:30
    * @param
    * @return
    *
    */
    void addDesc(PushChannel pushChannel);
   /**
   * @todo 更新推送渠道信息
   * @date 20/05/20 11:07
   * @param pushChannel 渠道对象
   * @return 成功true,失败false
   */
    boolean update(PushChannel pushChannel);

    /**
    * @todo 删除推送渠道
    * @date 20/05/20 11:03
    * @param id  渠道id
    * @return 成功true,失败false
    *
    */
    boolean delete(int id);

    /**
    * @todo 根据渠道id,选择渠道对象
    * @date 20/05/20 18:26
    * @param
    * @return
    *
    */
     PushChannel getChannelById(String id);

     /**
     * @todo 根据用户id 和 渠道名 搜索渠道对象
     * @date 20/05/20 18:26
     * @param 用户id 渠道名称
     * @return
     *
     */
     IPage<PushChannel> listChannelsByName(String userId,String type,String name,IPage<PushChannel> page);

     /**
     * @todo 计算用户 某个类型渠道的总个数
     * @date 20/05/21 17:53
     * @param 用户id  渠道类型
     * @return
     *
     */
     Integer calRecordsByType(String userId,String type);

     /**
     * @todo 根据用户id,渠道类型，返回分页信息
     * @date 20/05/21 18:51
     * @param
     * @return
     *
     */
     IPage<PushChannel>  listPagesByType(IPage<PushChannel> page,String userId,String type);

     /**
     * @todo 根据状态 类型 获取分页信息
     * @date 20/06/07 21:41
     * @param
     * @return
     *
     */
     IPage<PushChannel>  listPagesByTypeAndStatus(IPage<PushChannel> page,String userId,String type,String status);


}
