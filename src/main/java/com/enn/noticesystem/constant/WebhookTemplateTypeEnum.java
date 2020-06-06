package com.enn.noticesystem.constant;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/06/06 16:02
 * Version: 1.0
 */
public enum WebhookTemplateTypeEnum {
//    1 text文本 2 markdown 3 图片 4 图文消息
    TEXT(1,"Text"),
    MARKDOWN(2,"Markdown"),
    IMAGE(3,"图片"),
    NEWS(4,"图文消息");

    private final Integer code;
    private final String desc;

    WebhookTemplateTypeEnum(final Integer value, final String desc) {
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
        WebhookTemplateTypeEnum[] values = values();
        for (WebhookTemplateTypeEnum value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
