package com.enn.noticesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enn.noticesystem.domain.MsgTemplate;
import com.enn.noticesystem.domain.PushChannel;

import java.util.List;
import java.util.Map;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 18:08
 * Version: 1.0
 */
public interface MsgTemplateService extends IService<MsgTemplate> {
   /**
   * @todo 创建模板页面需要的数据
   * @date 20/06/06 17:01
   * @param
   * @return
   *
   */
    Map<String,Object> addV(String type);

    /**
     * @todo 新增模板
     * @date 20/05/20 11:02
     * @param msgTemplate 渠道对象
     * @return 插入成功返回id,失败返回-1
     *
     */
    Integer add(MsgTemplate msgTemplate);
    /**
    * @todo 添加template 描述
    * @date 20/06/06 19:11
    * @param
    * @return
    *
    */
    void addDesc(MsgTemplate msgTemplate);
    /**
     * @todo 更新模板
     * @date 20/05/20 11:07
     * @param msgTemplate 渠道对象
     * @return 成功true,失败false
     */
    boolean update(MsgTemplate msgTemplate);

    /**
     * @todo 删除模板
     * @date 20/05/20 11:03
     * @param id  渠道id
     * @return 成功true,失败false
     *
     */
    boolean delete(int id);

   /**
   * @todo 根据模板id,选择模板对象
   * @date 20/05/21 12:35
   * @param id 模板id
   * @return MsgTemplate 对象
   *
   */
    MsgTemplate getTemplateById(String id);

   /**
   * @todo 根据用户id,模板名 搜索模板
   * @date 20/05/21 12:37
   * @param 登录用户id  模板名称
   * @return
   *
   */
   IPage<MsgTemplate> listTemplatesByName(String userId,String type,String name,IPage<MsgTemplate> page);

    /**
     * @todo 计算用户 某个类型模板的总个数
     * @date 20/05/21 17:53
     * @param 用户id  渠道类型
     * @return
     *
     */
    /**
    * @todo 根据模板名称、类别 用户 计算记录总数
    * @date 20/06/04 10:18
    * @param
    * @return
    *
    */
    Integer calRecordsByName(String userId,String type,String name);

    Integer calRecordsByType(String userId,String type);
    /**
     * @todo 根据用户id,模板类型，返回分页信息
     * @date 20/05/21 18:51
     * @param
     * @return
     *
     */
    IPage<MsgTemplate> listPagesByType(IPage<MsgTemplate> page, String userId, String type);


    /**
    * @todo 根据模块状态 类型 获取模板分页list
    * @date 20/06/07 21:51
    * @param
    * @return
    *
    */
    IPage<MsgTemplate> listPagesByTypeAndStatus(IPage<MsgTemplate> page, String userId, String type,String status);


}
