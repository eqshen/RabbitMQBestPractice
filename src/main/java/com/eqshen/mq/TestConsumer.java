package com.eqshen.mq;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;

import com.eqshen.config.RabbitMqConfig;
import com.eqshen.entity.MqRecord;
import com.eqshen.enums.MqRecordStatusEnum;
import com.eqshen.service.MqRecordService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @Auther: eqshen
 * @Description 消费者
 * @Date: 2020/3/10 18:07
 */
@Component
@Slf4j
public class TestConsumer {

    @Autowired
    private MqRecordService mqRecordService;


    @RabbitListener(queues = {RabbitMqConfig.TEST_QUEUE})
    public void consumeApproveRequest(Channel channel, Message msg) throws Exception {
        String msgId = msg.getMessageProperties().getMessageId();
        String msgBody = new String(msg.getBody());
        try{
            MqRecord mqRecord = this.mqRecordService.queryByUniqueKey(msgId);
            //先检查是否已经消费过
            if(!checkRepetableMsg(mqRecord,msgBody)){
                return;
            }

            //msgBody消息内容，调用具体的业务逻辑
            //...

            log.info("[MQ]消费消息成功:{}-{}",msgId,msgBody);
            this.mqRecordService.updateRecordStatus(msgId, MqRecordStatusEnum.CONSUMED.getCode());
            //每个参数的作用
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            log.error("[MQ]消费消息失败:{}-{}",msgId,msgBody);
            throw e;
        }
    }

    /**
     * 检查重复检查的消息
     * @param mqRecord
     * @return
     */
    private boolean checkRepetableMsg(MqRecord mqRecord,String msgBody){
        if(mqRecord != null){
            if(mqRecord.getStatus() == MqRecordStatusEnum.CONSUMED.getCode()){
                log.warn("重复的请求，忽略 {}",mqRecord.getMsgId());
                return false;
            }
        }else{
            log.warn("未知的申请 {}",msgBody);
            return false;
        }
        return true;
    }
}
