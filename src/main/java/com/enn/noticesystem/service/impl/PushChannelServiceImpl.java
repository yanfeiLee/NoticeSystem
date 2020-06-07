package com.enn.noticesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enn.noticesystem.constant.TemplateChannelStatusEnum;
import com.enn.noticesystem.constant.PushChannelTypeEnum;
import com.enn.noticesystem.dao.mapper.PushChannelMapper;
import com.enn.noticesystem.domain.PushChannel;
import com.enn.noticesystem.service.PushChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 11:28
 * Version: 1.0
 */
@Service
@Slf4j
@Transactional
public class PushChannelServiceImpl extends ServiceImpl<PushChannelMapper, PushChannel> implements PushChannelService {

    /**
     * @param userId 渠道类型
     * @return
     * @todo lambda查询器
     * @date 20/05/21 18:30
     */
    private LambdaQueryWrapper<PushChannel> filterUserAndType(String userId, String type) {
        LambdaQueryWrapper<PushChannel> pushChannelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pushChannelLambdaQueryWrapper.and(lqw -> lqw.eq(PushChannel::getCreatorId, userId)
                .eq(PushChannel::getType,type)).orderByDesc(PushChannel::getCreatedTime);;

        return pushChannelLambdaQueryWrapper;
    }

    @Override
    public Integer add(PushChannel pushChannel) {
        log.info("添加渠道");
        boolean res = this.save(pushChannel);
        if (res) {
            return pushChannel.getId();
        } else {
            return -1;
        }
    }

    @Override
    public void addDesc(PushChannel pushChannel) {
        pushChannel.setTypeDesc(PushChannelTypeEnum.getDescByCode(pushChannel.getType()));
        pushChannel.setStatusDesc(TemplateChannelStatusEnum.getDescByCode(pushChannel.getStatus()));
    }

    @Override
    public boolean update(PushChannel pushChannel) {
        log.info("更新渠道数据，id=" + pushChannel.getId());
        return this.updateById(pushChannel);
    }


    @Override
    public boolean delete(int id) {
        log.info("删除渠道,id=" + id);
        boolean res = this.removeById(id);
        return res;
    }

    @Override
    public PushChannel getChannelById(String id) {
        log.info("获取id="+id+"的渠道信息");
        PushChannel res = this.getById(id);
        return res;
    }

    @Override
    public IPage<PushChannel> listChannelsByName(String userId, String type, String name, IPage<PushChannel> page) {
        LambdaQueryWrapper<PushChannel> pushChannelLambdaQueryWrapper = filterUserAndType(userId, type);
        LambdaQueryWrapper<PushChannel> allWrapper = pushChannelLambdaQueryWrapper.and(lqw -> lqw.eq(PushChannel::getName, name));

        IPage<PushChannel> res = this.page(page, allWrapper);

        log.info("根据渠道名，获取渠道list:size=" +res.getRecords().size());
        return res;
    }


    @Override
    public Integer calRecordsByType(String userId, String type) {

        log.info("计算用户" + userId + "渠道类型" + type + "的个数");

        int res = this.count(filterUserAndType(userId, type));
        return res;
    }

    @Override
    public IPage<PushChannel> listPagesByType(IPage<PushChannel> page, String userId, String type) {
        log.info("获取type="+type+"的分页信息");
        IPage<PushChannel> res = this.page(page, filterUserAndType(userId, type));
        return res;
    }

}
