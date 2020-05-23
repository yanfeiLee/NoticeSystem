package com.enn.noticesystem.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 10:06
 * Version: 1.0
 */
@Data
@TableName("ns_push_channel")
public class PushChannel {
    private static final long serialVersionUID = 1L;
//   公共属性
    /**
     * 渠道id（自增主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 渠道名称
     */
    private String name;

    /**
     * 渠道描述
     */
    private String description;

    /**
     * 渠道类型：1 机器人 2 短信 3 站内信 4 邮件
     */

    private Integer type;

    /**
     * 渠道创建者(关联user表主键)
     */
    private Integer creatorId;

    /**
     * 渠道状态：0 关闭 1启动
     */
    private Integer status;

    /**
     * 删除标志：0 正常 1 删除
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Boolean deletedFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;


//    机器人相关属性
    /**
     * 企业微信推送地址
     */
    private String robotWebhook;

    /**
     * 机器人所在群(多个群用逗号分割)
     */
    private String robotGroupInc;

}
