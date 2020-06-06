package com.enn.noticesystem.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 17:46
 * Version: 1.0
 */
@TableName("ns_msg_template")
@Data
public class MsgTemplate {
    private static final long serialVersionUID = 1L;
//    公共字段
    /**
     *  模板id（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     *  模板名称
     */
    private String name;
    /**
     *  模版类型 1机器人 2 短信  3站内信  4邮件
     */
    private Integer type;
    @TableField(exist = false)
    private String typeDesc;
    /**
     *  模板描述
     */
    private String description;
    /**
     *  模板状态：0关闭 1开启
     */
    private Integer status;
    @TableField(exist = false)
    private String statusDesc;

    /**
     *  创建者id(引用用户表主键)
     */
    private Integer creatorId;

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
    
//    机器人模板相关字段
    /**
     * 机器人推送模板（其中包含 请求的api地址、请求的资源名、用户自定义的指标小标题）
     */
    private String robotPushTemplate;
    
    /**
     *  机器人通过webhook推送时的模板类型：1text 文本 2 markdown 3 图片 4 图文消
     */
    private Integer robotPushType;
    @TableField(exist = false)
    private String robotPushTypeDesc;
    
    
}
