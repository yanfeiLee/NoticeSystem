package com.enn.noticesystem.constant;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/19 10:16
 * Version: 1.0
 */
public enum RequestType {
    POST(1,"POST 请求"),
    GET(2,"GET 请求");


    private final Integer code;
    private final String desc;

    RequestType(final Integer value, final String desc) {
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
        RequestType[] values = values();
        for (RequestType value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
