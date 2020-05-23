package com.enn.noticesystem.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/18 17:01
 * Version: 1.0
 */
@Slf4j
public class MyMetaObjectConfig implements MetaObjectHandler{

    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("MetaObject 插入数据");
        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName("createdTime", now, metaObject);
        this.setFieldValByName("deletedFlag", false, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("MetaObject 更新数据");
        this.setFieldValByName("updatedTime", LocalDateTime.now(), metaObject);
    }
}
