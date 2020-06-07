package com.enn.noticesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enn.noticesystem.constant.PushChannelTypeEnum;
import com.enn.noticesystem.constant.TemplateChannelStatusEnum;
import com.enn.noticesystem.constant.WebhookTemplateTypeEnum;
import com.enn.noticesystem.dao.mapper.MsgTemplateMapper;
import com.enn.noticesystem.domain.MsgTemplate;
import com.enn.noticesystem.service.MsgTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param userId 模板类型
     * @return
     * @todo LambdaQuery 查询器
     * @date 20/05/21 18:37
     */
    private LambdaQueryWrapper<MsgTemplate> filterUserAndType(String userId, String type) {
        LambdaQueryWrapper<MsgTemplate> msgTemplateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        msgTemplateLambdaQueryWrapper.and(lqw -> lqw.eq(MsgTemplate::getCreatorId, userId)
                .eq(MsgTemplate::getType, type)
        ).orderByDesc(MsgTemplate::getCreatedTime);
        return msgTemplateLambdaQueryWrapper;
    }

    @Override
    public Map<String, Object> addV(String type) {
        Map<String,Object> res =new HashMap<>();
        if(type.equals(PushChannelTypeEnum.ROBOT.getCode().toString())){
            List<Map<String,Object>> webhookTemplateTypeList = new ArrayList<>();
            Map<String,Object> text = new HashMap<>();
            text.put("code",WebhookTemplateTypeEnum.TEXT.getCode());
            text.put("desc", WebhookTemplateTypeEnum.TEXT.getDesc());
            webhookTemplateTypeList.add(text);

            Map<String,Object> markdown = new HashMap<>();
            markdown.put("code",WebhookTemplateTypeEnum.MARKDOWN.getCode());
            markdown.put("desc", WebhookTemplateTypeEnum.MARKDOWN.getDesc());
            webhookTemplateTypeList.add(markdown);
            Map<String,Object> image = new HashMap<>();
            image.put("code",WebhookTemplateTypeEnum.IMAGE.getCode());
            image.put("desc", WebhookTemplateTypeEnum.IMAGE.getDesc());
            webhookTemplateTypeList.add(image);
            Map<String,Object> news = new HashMap<>();
            news.put("code",WebhookTemplateTypeEnum.NEWS.getCode());
            news.put("desc", WebhookTemplateTypeEnum.NEWS.getDesc());
            webhookTemplateTypeList.add(news);
            res.put("webhookTemplateList", webhookTemplateTypeList);
        }
        //todo 其他模板类型 数据
        return res;
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
    public void addDesc(MsgTemplate msgTemplate) {
        msgTemplate.setTypeDesc(PushChannelTypeEnum.getDescByCode(msgTemplate.getType()));
        msgTemplate.setStatusDesc(TemplateChannelStatusEnum.getDescByCode(msgTemplate.getStatus()));
        if(msgTemplate.getType()==PushChannelTypeEnum.ROBOT.getCode()){
            msgTemplate.setRobotPushTypeDesc(WebhookTemplateTypeEnum.getDescByCode(msgTemplate.getRobotPushType()));
        }
        //todo 其他渠道描述信息
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
    public MsgTemplate getTemplateById(String id) {
        log.info("模板信息:id=" + id);
        MsgTemplate res = this.getById(id);
        return res;
    }

    @Override
    public IPage<MsgTemplate> listTemplatesByName(String userId, String type, String name, IPage<MsgTemplate> page) {
        LambdaQueryWrapper<MsgTemplate> msgTemplateLambdaQueryWrapper = filterUserAndType(userId, type);
        LambdaQueryWrapper<MsgTemplate> allWarpper = msgTemplateLambdaQueryWrapper.and(lqw ->
                lqw.eq(MsgTemplate::getName, name));
        IPage<MsgTemplate> res = this.page(page, allWarpper);
        log.info("用户id=" + userId + ",模板名为：" + name + "的模板有 :size=" + res.getRecords().size());
        return res;
    }

    @Override
    public Integer calRecordsByName(String userId, String type, String name) {
        log.info("计算用户id=" + userId + ",type=" + type + "name=" + name + "的记录总数");
        LambdaQueryWrapper<MsgTemplate> msgTemplateLambdaQueryWrapper = filterUserAndType(userId, type);
        LambdaQueryWrapper<MsgTemplate> allWarpper = msgTemplateLambdaQueryWrapper.and(lqw -> lqw.eq(MsgTemplate::getName, name));
        int res = this.count(allWarpper);
        return res;
    }

    @Override
    public Integer calRecordsByType(String userId, String type) {
        log.info("计算用户id=" + userId + ",type=" + type + "总数");

        int res = this.count(filterUserAndType(userId, type));

        return res;
    }

    @Override
    public IPage<MsgTemplate> listPagesByType(IPage<MsgTemplate> page, String userId, String type) {
        log.info("查询type=" + type + "的分页信息");
        IPage<MsgTemplate> res = this.page(page, filterUserAndType(userId, type));
        return res;
    }


}
