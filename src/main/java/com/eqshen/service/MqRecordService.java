package com.eqshen.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eqshen.entity.MqRecord;
import com.eqshen.mapper.MqRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Auther: eqshen
 * @Description
 * @Date: 2020/5/14 15:06
 */
@Service
@Slf4j
public class MqRecordService extends ServiceImpl<MqRecordMapper, MqRecord> {

    public MqRecord queryByUniqueKey(String msgId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("msg_id",msgId);
        return baseMapper.selectOne(wrapper);
    }

    public boolean businessSave(MqRecord mqRecord) {
        MqRecord oldMqRecord = this.queryByUniqueKey(mqRecord.getMsgId());
        if(oldMqRecord == null){
            log.info("保存MqRecord {}",mqRecord);
            return this.save(mqRecord);
        }else{
            oldMqRecord.setDataId(mqRecord.getDataId());
            oldMqRecord.setMsgType(mqRecord.getMsgType());
            oldMqRecord.setStatus(mqRecord.getStatus());
            oldMqRecord.setUpdateTime(LocalDateTime.now());
            log.info("更新MqRecord {}",mqRecord);
            return this.updateById(oldMqRecord);
        }
    }

    public int updateRecordStatus(String msgId, int status) {
        return this.baseMapper.updateStauts(msgId,status);
    }

    public int updateRecordStatus(String msgId, int oldStatus, int status) {
        return  this.baseMapper.updateStautsWithCondition(msgId,oldStatus,status);
    }


}
