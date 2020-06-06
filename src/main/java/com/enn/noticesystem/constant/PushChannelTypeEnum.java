package com.enn.noticesystem.constant;

import com.mchange.v2.holders.ThreadSafeIntHolder;
import javafx.scene.shape.VLineTo;
import lombok.Data;

import java.lang.management.ThreadInfo;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/06/06 15:46
 * Version: 1.0
 */
public enum PushChannelTypeEnum {
    //2短信 3站内信 4 邮件
    ROBOT(1,"机器人"),
    MESSAGE(2,"短信"),
    SITEMSG(3,"站内信"),
    EMAIL(4,"邮件");

    private final Integer code;
    private final String desc;

    PushChannelTypeEnum(final Integer value, final String desc) {
        this.code=value;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(Integer code){
        PushChannelTypeEnum[] values = values();
        for (PushChannelTypeEnum value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
