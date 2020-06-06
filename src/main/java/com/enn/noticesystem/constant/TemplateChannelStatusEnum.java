package com.enn.noticesystem.constant;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/06/06 19:51
 * Version: 1.0
 */
public enum TemplateChannelStatusEnum {
    CLOSE(0,"停用"),
    START(1,"启用");

    private final Integer code;
    private final String desc;

    TemplateChannelStatusEnum(final Integer value, final String desc) {
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
        TemplateChannelStatusEnum[] values = values();
        for (TemplateChannelStatusEnum value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
