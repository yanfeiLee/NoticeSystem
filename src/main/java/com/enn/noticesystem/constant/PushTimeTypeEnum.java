package com.enn.noticesystem.constant;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/06/06 15:57
 * Version: 1.0
 */
public enum PushTimeTypeEnum {
//1 立即发送 2 周期发送
    IMMEDIATELY(1,"立即执行"),
    PERIODIC(2,"周期执行");

    private final Integer code;
    private final String desc;

    PushTimeTypeEnum(final Integer value, final String desc) {
        this.code=value;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getEnumName() {
        return name();
    }

    public static String getDescByCode(Integer code){
        PushTimeTypeEnum[] values = values();
        for (PushTimeTypeEnum value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
