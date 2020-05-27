package com.enn.noticesystem.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/27 18:20
 * Version: 1.0
 */
@TableName("ns_msg")
@Data
public class Msg {

    private static final long serialVersionUID = 1L;

    /**
     *  模板id（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *  调度任务id
     */
    private Integer jobId;


    /**
     *  消息标题
     */
    private String title;
    /**
     *  消息内容
     */
    private String content;

    /**
     *  消息生成时间
     */
    private LocalDateTime genTime;

    /**
     *  获取结果
     */
    private Boolean pullRes;

    /**
     *  推送结果
     */
    private Boolean pushRes;

}
