package com.eqshen.mq;

import com.eqshen.config.RabbitMqConfig;
import com.eqshen.entity.MqRecord;
import com.eqshen.enums.MqRecordStatusEnum;
import com.eqshen.service.MqRecordService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Auther: eqshen
 * @Description 风控请求消息生产者
 * @Date: 2020/3/10 17:56
 */
@Component
@Slf4j
public class TestProducer {

    @Autowired
    private MqRecordService mqRecordService;

    private RabbitTemplate rabbitTemplate;

    @Resource(name = "rabbitTemplateCallback")
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        //消息从生产者到达exchange时返回ack，消息未到达exchange返回nack；
        this.rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack){
                    //更新消息发送记录表的状态，采用乐观锁，
                    mqRecordService.updateRecordStatus(correlationData.getId(),
                            MqRecordStatusEnum.SEND.getCode(),MqRecordStatusEnum.SEND_SUCCESS.getCode());
                    log.info("[MQ]消息发送结果确认 ack:{},cause:{},correlationData:{}",ack,cause,correlationData);
                }else{
                    log.error("[MQ]消息发送失败，ack:{},cause:{},correlationData:{}",ack,cause,correlationData);
                }
            }
        });

        //消息进入exchange但未进入queue时会被调用。
        this.rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.error("[MQ]消息未成功进入queue:exchange:{},route:{},replyCode:{},replyText:{},message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
    }


    /**
     * @param reqId  approveInfo自增id字段
     * @param msg
     */
    @Transactional
    public void sendRequest(long reqId,String msg){
        try{
            String key = UUID.randomUUID().toString().replaceAll("-","");
            MessageProperties mp = new MessageProperties();
            mp.setMessageId(key);
            Message msgObj = new Message(msg.getBytes(),mp);
            //消息唯一键
            CorrelationData correlationData = new CorrelationData(key);

            //保存消息发送记录
            MqRecord mqRecord = new MqRecord();
            mqRecord.setMsgType("test");
            mqRecord.setMsgId(key);
            mqRecord.setStatus(MqRecordStatusEnum.SEND.getCode());
            mqRecord.setDataId(reqId);
            this.mqRecordService.businessSave(mqRecord);

            //Topic模式发送
            this.rabbitTemplate.convertAndSend(RabbitMqConfig.TEST_TOPIC_EXCHANGE,
                    RabbitMqConfig.TEST_ROUTING_KEY,msgObj,correlationData);
            log.info("[MQ]消息发送成功 {},{}",key,msg);

        }catch (Exception e){
            log.error("[MQ]消息发送失败 {},{}",reqId,msg,e);
            throw e;
        }
    }

}

