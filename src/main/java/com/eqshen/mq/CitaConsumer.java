package com.eqshen.mq;

import com.eqshen.config.RabbitMqConfig;
import com.eqshen.entity.MqRecord;
import com.eqshen.enums.MqRecordStatusEnum;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Auther: eqshen
 * @Description 消费者
 * @Date: 2020/3/10 18:07
 */
@Component
@Slf4j
public class CitaConsumer {

//    @Autowired
//    private MqRecordService mqRecordService;


    @RabbitListener(queues = {RabbitMqConfig.TEST_QUEUE})
    public void consumeApproveRequest(Channel channel, Message msg) throws Exception {
        String msgId = msg.getMessageProperties().getMessageId();
        byte[] bytes = msg.getBody();
        try{
            //


            //ack
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            log.error("[MQ]消费消息失败:{}-{}",msgId,bytes.length);
            throw e;
        }
    }

}
