package com.enn.noticesystem.constant;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/06/06 16:15
 * Version: 1.0
 */
public enum TaskExecStatusEnum {

//    任务执行状态：0 未执行  1 成功  2 失败
    WATIING(0,"未执行"),
    SUCCESS(1,"正常"),
    FAILURES(2,"失败");


    private final Integer code;
    private final String desc;

    TaskExecStatusEnum(final Integer value, final String desc) {
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
        TaskExecStatusEnum[] values = values();
        for (TaskExecStatusEnum value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
