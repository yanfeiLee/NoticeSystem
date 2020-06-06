package com.enn.noticesystem.constant;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/06/06 16:11
 * Version: 1.0
 */
public enum TaskStatusEnum {
    //    任务状态：0 未启动  1 运行中  2 已暂停
    WATIING(0, "未启动"),
    RUNNING(1, "运行中"),
    PAUSE(2, "已暂停");


    private final Integer code;
    private final String desc;

    TaskStatusEnum(final Integer value, final String desc) {
        this.code = value;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(Integer code){
        TaskStatusEnum[] values = values();
        for (TaskStatusEnum value : values) {
            if(value.code == code){
                return value.desc;
            }
        }
        return "";
    }
}
