package com.eqshen.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @Auther: eqshen
 * @Description
 * @Date: 2020/3/10 17:39
 */
@Configuration
public class RabbitMqConfig {
    public static final String TEST_QUEUE = "approveRequestQueue";

    public static final String TEST_TOPIC_EXCHANGE = "approveRequestExchange_topic";

    public static final String TEST_ROUTING_KEY = "approveRequestRouting";

    @Value("${spring.rabbitmq.addresses}")
    private String addresses;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-confirm-type}")
    private String publisherConfirmType;

    @Value("${spring.rabbitmq.publisher-returns}")
    private Boolean publisherConfirmReturns;


    @Bean
    public ConnectionFactory connectionFactory() {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        /** 如果要进行消息回调，则这里必须要设置*/
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.valueOf(publisherConfirmType.toUpperCase()));
        connectionFactory.setPublisherReturns(publisherConfirmReturns);
        return connectionFactory;
    }

    //支持回调的template
    @Bean("rabbitTemplateCallbackDemo")
    /** 因为要设置回调类，所以应是prototype类型，如果是singleton类型，则回调类为最后一次设置 */
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplateCallBackDemo() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                //这里写确认逻辑
            }
        });
        return template;
    }

    //支持回调的template
    @Bean("rabbitTemplateCallback")
    public RabbitTemplate rabbitTemplateCallBack() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }


    @Bean
    public Queue approveRequestQueue(){
        return new Queue(TEST_QUEUE);
    }

    @Bean
    public TopicExchange approveRequestExchange() {
        return new TopicExchange(TEST_TOPIC_EXCHANGE);
    }

    @Bean
    public Binding bindingApproveReqEx(Queue reqQueue, TopicExchange reqExchange){
        return BindingBuilder.bind(reqQueue).to(reqExchange).with(TEST_ROUTING_KEY);
    }




}
