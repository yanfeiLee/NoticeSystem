package com.enn.noticesystem.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enn.noticesystem.domain.ScheduleJob;
import com.enn.noticesystem.domain.vo.ScheduleJobVO;
import org.apache.ibatis.annotations.Select;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/18 14:36
 * Version: 1.0
 */
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {

    @Select("SELECT \n" +
            "\tjob.*,\n" +
            "\tchannel.`name` channel_name,\n" +
            "\tchannel.robot_group_inc channel_robot_group_inc,\n" +
            "\tchannel.robot_webhook channel_robot_webhook,\n" +
            "\ttemplate.`name` template_name,\n" +
            "\ttemplate.robot_push_template template_robot_push_template,\n" +
            "\ttemplate.robot_push_type template_robot_push_type\t\n" +
            "FROM\n" +
            "\tns_schedule_job job\n" +
            "\tLEFT JOIN ns_msg_template template ON job.msg_template_id = template.id\n" +
            "\tLEFT JOIN ns_push_channel channel ON job.push_channel_id = channel.id\n" +
            "WHERE\tjob.id=#{id}")
    ScheduleJobVO getScheduleJobDetail(Integer id);
}
