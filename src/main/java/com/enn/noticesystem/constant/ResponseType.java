package com.enn.noticesystem.constant;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/06/12 16:33
 * Version: 1.0
 */
public enum ResponseType {
    JSON(1," JSON 格式响应"),
    XML(2,"XML 格式响应");


    private final Integer code;
    private final String desc;

    ResponseType(final Integer value, final String desc) {
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
        ResponseType[] values = values();
        for (ResponseType value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
