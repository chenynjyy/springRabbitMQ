package com.demo.spring.rabbitmq.config;

import com.demo.spring.rabbitmq.util.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by chenyunan on 2017/7/20.
 */
@Configuration
public class RabbitConfig {

    static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);

    /**
     * 连接工厂
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);//单个连接中的通道
//        connectionFactory.setPublisherConfirms(true);//发送broker需要确认
        connectionFactory.setPublisherReturns(true);//待查
        connectionFactory.setHost("192.168.4.157");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public SimpleMessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }

    /**
     * 消息发送有关
     */
    @Configuration
    public static class RabbitTemplateConfig {

        @Autowired
        ConnectionFactory connectionFactory;
        @Autowired
        MessageConverter messageConverter;

        @Bean
        public RabbitTemplate rabbitTemplate() {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.setMessageConverter(messageConverter);
//            rabbitTemplate.isReturnListener();
//            rabbitTemplate.isConfirmListener();
            rabbitTemplate.setReplyTimeout(1000);
            return rabbitTemplate;
        }

        @Bean
        public RabbitAdmin rabbitAdmin() {
            return new RabbitAdmin(connectionFactory);
        }

    }

    /**
     * 消息接收有关
     */
    @Configuration
    public static class RabbitListenerConfig {

        @Autowired
        ConnectionFactory connectionFactory;

        @Autowired
        MessageConverter messageConverter;

        @Value("${com.demo.spring.rabbitmq.workid:123}")
        long workId;
        @Value("${com.demo.spring.rabbitmq.datacenterid:321}")
        long dataCenterId;

        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(workId, dataCenterId);

        /**
         * 监听器工厂/队列工厂
         * @return
         */
        @Bean
        public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
            SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
            simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
            simpleRabbitListenerContainerFactory.setTxSize(10);
            simpleRabbitListenerContainerFactory.setTransactionManager(rabbitTransactionManager());
            simpleRabbitListenerContainerFactory.setConsumerTagStrategy((s) -> {
                return String.valueOf(snowflakeIdWorker.nextId());
            });
            simpleRabbitListenerContainerFactory.setErrorHandler((Throwable throwable) -> {
                logger.error("error message : " + throwable.getMessage());
                for(StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                    logger.error(stackTraceElement.getClassName() + " : " +
                            stackTraceElement.getMethodName() + " : " +
                            stackTraceElement.getLineNumber());
                }
            });
            simpleRabbitListenerContainerFactory.setMessageConverter(messageConverter);
            simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(10);
            /**
             * NONE = no acks will be sent (incompatible with channelTransacted=true). RabbitMQ calls this "autoack" because the broker assumes all messages are acked without any action from the consumer.
               MANUAL = the listener must acknowledge all messages by calling Channel.basicAck().
               AUTO = the container will acknowledge the message automatically, unless the MessageListener throws an exception.
                 Note that acknowledgeMode is complementary to channelTransacted -
                 if the channel is transacted then the broker requires a commit notification in addition to the ack.
                 This is the default mode. See also txSize.
             */
            simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//手动确认消息是否正常
            return simpleRabbitListenerContainerFactory;
        }

        @Bean
        public RabbitTransactionManager rabbitTransactionManager() {
            RabbitTransactionManager rabbitTransactionManager = new RabbitTransactionManager(connectionFactory);
            rabbitTransactionManager.setDefaultTimeout(10000);
            rabbitTransactionManager.setValidateExistingTransaction(true);
            rabbitTransactionManager.setFailEarlyOnGlobalRollbackOnly(true);
            rabbitTransactionManager.setGlobalRollbackOnParticipationFailure(true);
            rabbitTransactionManager.setNestedTransactionAllowed(true);
            rabbitTransactionManager.setRollbackOnCommitFailure(true);
            return rabbitTransactionManager;
        }

    }



}
