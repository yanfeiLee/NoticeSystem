package com.enn.noticesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enn.noticesystem.dao.mapper.MsgMapper;
import com.enn.noticesystem.domain.Msg;
import com.enn.noticesystem.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/27 18:33
 * Version: 1.0
 */
@Service
@Slf4j
@Transactional
public class MsgServiceImpl extends ServiceImpl<MsgMapper,Msg> implements MsgService {


    @Override
    public Integer add(Msg msg) {
        log.info("添加消息");
        boolean res = this.save(msg);
        if (res) {
            return msg.getId();
        } else {
            return -1;
        }
    }

    @Override
    public boolean update(Msg msg) {
        log.info("更新消息数据，id=" + msg.getId());
        return this.updateById(msg);

    }

    @Override
    public Msg getMsgById(Integer id) {
        log.info("获取id="+id+"的消息对象");
        Msg msg = this.getById(id);
        return msg;
    }

    @Override
    public List<Msg> listMsgByJobId(Integer jobId) {
        log.info("获取jobid="+jobId+"的消息");
        LambdaQueryWrapper<Msg> msgLambdaQueryWrapper = new LambdaQueryWrapper<>();
        msgLambdaQueryWrapper.and(lqw->lqw.eq(Msg::getJobId ,jobId ));
        List<Msg> list = this.list(msgLambdaQueryWrapper);
        return list;
    }

    @Override
    public List<Msg> listMsgByRes(Integer jobId, Boolean pullRes, Boolean pushRes) {
        log.info("获取jobId="+jobId+",pullRes="+pullRes+",pushRes="+pushRes+"的数据");
        LambdaQueryWrapper<Msg> msgLambdaQueryWrapper = new LambdaQueryWrapper<>();
        msgLambdaQueryWrapper.and(lqw->lqw.eq(Msg::getJobId , jobId)
                .eq(Msg::getPullRes , pullRes).eq(Msg::getPushRes ,pushRes));
        List<Msg> list = this.list(msgLambdaQueryWrapper);
        return list;
    }
}
