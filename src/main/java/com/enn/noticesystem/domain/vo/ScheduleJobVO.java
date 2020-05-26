package com.enn.noticesystem.domain.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/22 15:06
 * Version: 1.0
 */
@Data
public class ScheduleJobVO implements Serializable {


    private static final long serialVersionUID = 3720899153454969715L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 任务描述
     */

    private String description;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 推送时间类型:1立即发送 2 周期发送
     */
    private int pushTimeType;


    /**
     * 推送渠道类型：1 机器人 2 短信 3 站内信 4 邮件
     */
    private int pushChannelType;

    /**
     * 推送渠道id （引用ns_push_channel表主键）
     */
    private int pushChannelId;

    /**
     * 渠道模板id（引用ns_msg_template表主键）
     */
    private int msgTemplateId;


    /**
     * 任务执行时，推送消息标题
     */
    private String subTitle;

    /**
     * 状态 1.启动 2.暂停
     */
    private int status;

    /**
     * 任务执行状态: 0 未执行 1 成功 2 失败
     */
    private int execStatus;

    /**
     * 是否删除 0.否 1.是
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Boolean deletedFlag;

    /**
     * 创建人id(引用用户表中的主键)
     */
    private Integer creatorId;

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


//    关联表的字段
    /**
     *  渠道名
     */
    private String channelName;
    /**
     *  机器人所在企业微信群
     */
    private String channelRobotGroupInc;

    /**
     *  机器人webhook地址
     */
    private String channelRobotWebhook;

    /**
     *  模板名
     */
    private String templateName;
    /**
     *  机器人推送模板-json格式字符串
     */
    private String templateRobotPushTemplate;

    /**
     *  机器人推送类型(1text 文本 2 markdown 3 图片 4 图文消息)
     */
    private Integer templateRobotPushType;
}
