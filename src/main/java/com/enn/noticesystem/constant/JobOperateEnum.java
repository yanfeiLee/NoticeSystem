package com.enn.noticesystem.constant;

import java.io.Serializable;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/18 17:16
 * Version: 1.0
 */
public enum JobOperateEnum {

    START(1, "启动"),
    PAUSE(2, "暂停"),
    DELETE(3, "删除");

    private final Integer code;
    private final String desc;

    JobOperateEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Serializable getcode() {
        return this.code;
    }

    // Jackson 注解为 Jsoncode 返回中文 json 描述
    public String getDesc() {
        return this.desc;
    }

    public String getEnumName() {
        return name();
    }

    public static String getDescByCode(Integer code){
        JobOperateEnum[] values = values();
        for (JobOperateEnum value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
