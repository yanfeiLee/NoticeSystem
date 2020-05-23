package com.enn.noticesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enn.noticesystem.dao.mapper.MsgTemplateMapper;
import com.enn.noticesystem.domain.MsgTemplate;
import com.enn.noticesystem.service.MsgTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 18:27
 * Version: 1.0
 */
@Service
@Transactional
@Slf4j
public class MsgTemplateServiceImpl extends ServiceImpl<MsgTemplateMapper, MsgTemplate> implements MsgTemplateService {

    /**
     * @param 用户id 模板类型
     * @return
     * @todo LambdaQuery 查询器
     * @date 20/05/21 18:37
     */
    private LambdaQueryWrapper<MsgTemplate> filterUserAndType(String userId, String type) {
        LambdaQueryWrapper<MsgTemplate> msgTemplateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        msgTemplateLambdaQueryWrapper.and(lqw -> lqw.eq(MsgTemplate::getCreatorId, userId).eq(MsgTemplate::getType, type));
        return msgTemplateLambdaQueryWrapper;
    }

    @Override
    public Integer add(MsgTemplate msgTemplate) {
        log.info("添加模板");
        boolean res = this.save(msgTemplate);
        if (res) {
            return msgTemplate.getId();
        } else {
            return -1;
        }

    }

    @Override
    public boolean update(MsgTemplate msgTemplate) {
        log.info("更新渠道数据，id=" + msgTemplate.getId());
        return this.updateById(msgTemplate);
    }

    @Override
    public boolean delete(int id) {
        log.info("删除渠道,id=" + id);
        boolean res = this.removeById(id);
        return res;
    }

    @Override
    public List<MsgTemplate> listTemplatesByType(String userId, String type) {
        log.info("获取模板list: uid=" + userId + ";type=" + type);

        List<MsgTemplate> res = this.list(filterUserAndType(userId, type));
        log.info("type=" + type + " 的模板list:" + res.toString());
        return res;
    }

    @Override
    public MsgTemplate getTemplateById(String id) {

        MsgTemplate res = this.getById(id);
        log.info("模板信息" + res);
        return res;
    }


    @Override
    public List<MsgTemplate> listTemplatesByName(String userId, String name) {
        LambdaQueryWrapper<MsgTemplate> msgTemplateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        msgTemplateLambdaQueryWrapper.and(lqw -> lqw.eq(MsgTemplate::getCreatorId, userId).eq(MsgTemplate::getName, name));
        List<MsgTemplate> list = this.list(msgTemplateLambdaQueryWrapper);
        log.info("用户id=" + userId + ",模板名为：" + name + "的模板有 " + list.toString());
        return list;
    }

    @Override
    public Integer calRecordsByType(String userId, String type) {
        log.info("计算用户id="+userId+",type="+type+"总数");

        int res = this.count(filterUserAndType(userId, type));

        return res;
    }

    @Override
    public IPage<MsgTemplate> listPagesByType(IPage<MsgTemplate> page, String userId, String type) {
        log.info("查询type="+type+"的分页信息");
        IPage<MsgTemplate> res = this.page(page, filterUserAndType(userId, type));
        return res;
    }


}
