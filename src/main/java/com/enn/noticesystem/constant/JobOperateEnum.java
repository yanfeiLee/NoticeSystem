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

    private final Integer value;
    private final String desc;

    JobOperateEnum(final Integer value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Serializable getValue() {
        return this.value;
    }

    // Jackson 注解为 JsonValue 返回中文 json 描述
    public String getDesc() {
        return this.desc;
    }

    public String getEnumName() {
        return name();
    }
}
