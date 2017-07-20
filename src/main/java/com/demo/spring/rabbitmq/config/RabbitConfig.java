package com.demo.spring.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * Created by Administrator on 2017/7/18.
 */
//@Configuration
public class RabbitConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setPhase();
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);//同个线程请求，使用同一个管道连接
//        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setHost("192.168.4.157");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Value("${com.test.url:http://192.168.4.157:5672/}")
    String url;
    @Value("${com.test.username:guest}")
    String userName;
    @Value("${com.test.password:guest}")
    String password;

//    @Bean
//    public Client client() {
//        Client client = null;
//        try {
//            client = new Client(url, userName, password);
//            client.createUser("test", "test".toCharArray(), Arrays.asList("administrator"));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return client;
//    }

    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory());
        simpleRabbitListenerContainerFactory.setTransactionManager(rabbitTransactionManager());
//        simpleRabbitListenerContainerFactory.setPhase(1); //最好是Integer.MAX_VALUE
        simpleRabbitListenerContainerFactory.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String s) {
                return System.currentTimeMillis() + UUID.randomUUID().toString();
            }
        });
//        simpleRabbitListenerContainerFactory.setErrorHandler(new ErrorHandler() {
//            @Override
//            public void handleError(Throwable throwable) {
//                System.out.println(throwable.toString());
//            }
//        });
//        simpleRabbitListenerContainerFactory.createListenerContainer(null).setAfterReceivePostProcessors(new MessagePostProcessor() {
//            @Override
//            public Message postProcessMessage(Message message) throws AmqpException {
//                System.out.println(message);
//                return message;
//            }
//        });
        simpleRabbitListenerContainerFactory.setMessageConverter(simpleMessageConverter());
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
        simpleRabbitListenerContainerFactory.setTxSize(10);
        simpleRabbitListenerContainerFactory.setTransactionManager(rabbitTransactionManager());
        return simpleRabbitListenerContainerFactory;
    }

    @Bean
    public SimpleMessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }

    /**
     * 相当于业务队列参数的定义
     * @return
     */
    @Bean
    public AmqpAdmin amqpAdmin() {
//        MessageProperties
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        rabbitAdmin.declareExchange(exchange());
        rabbitAdmin.declareQueue(queue());
        rabbitAdmin.declareBinding(binding());
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
    }

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.directExchange("spring.test.direct.exchange")
                .durable(false)
                .internal()
                .build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.nonDurable("spring.test.queue.1").build();
    }

    @Bean
    public Binding binding() {
//        DestinationConfigurer destinationConfigurer = new DestinationConfigurer("spring.test.destination", Binding.DestinationType.QUEUE);
//        destinationConfigurer.to(exchange());
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with("spring.test.direct.exchange.routingkey")
                .noargs();
//        DirectExchange directExchange = new DirectExchange("spring.test.direct.exchange.1", false, false, null);
//        DirectExchangeRoutingKeyConfigurer configurer = new DirectExchangeRoutingKeyConfigurer(BindingBuilder.bind(exchange()), directExchange);
//        configurer.with("spring.test.direct.exchange.routingkey");
//        return DirectExchangeRoutingKeyConfigurer()
    }

//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
//        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
//        simpleMessageListenerContainer.setChannelTransacted(true);
//
//    }

    @Bean
    public RabbitTransactionManager rabbitTransactionManager() {
        RabbitTransactionManager rabbitTransactionManager = new RabbitTransactionManager(connectionFactory());
        rabbitTransactionManager.setDefaultTimeout(10000);
        rabbitTransactionManager.setValidateExistingTransaction(true);
        rabbitTransactionManager.setFailEarlyOnGlobalRollbackOnly(true);
        rabbitTransactionManager.setValidateExistingTransaction(true);
        rabbitTransactionManager.setGlobalRollbackOnParticipationFailure(true);
        rabbitTransactionManager.setNestedTransactionAllowed(true);
        rabbitTransactionManager.setRollbackOnCommitFailure(true);
        return rabbitTransactionManager;
    }

//    @Bean
//    public RabbitManagementTemplate rabbitManagementTemplate() {
////        RabbitAdmin;
////        RabbitAccessor;
////        RabbitAdminEvent;
////        RabbitGatewaySupport;
////        RabbitUtils;
////        RabbitController;
////        RabbitConfig;
////        RabbitResourceHolder;
////        RabbitListenerConfigUtils;
////        RabbitTransactionManager;
////        RabbitListenerEndpointRegistrar;
////        RabbitListenerEndpointRegistry;
//        RabbitManagementTemplate rabbitManagementTemplate = new RabbitManagementTemplate();
////        rabbitManagementTemplate.
//        return rabbitManagementTemplate;
//    }


//    @Bean
//    public AmqpTemplate amqpTemplate() {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                System.out.println(correlationData.getId() + ack + cause);
//            }
//        });
//        rabbitTemplate.execute(new ChannelCallback<String>() {
//
//            @Override
//            public String doInRabbit(Channel channel) throws Exception {
//                return null;
//            }
//        });
//        rabbitTemplate.
////        rabbitTemplate.isReturnListener()
//        rabbitTemplate.sendAndReceive(new Message("qq".getBytes(), new MessageProperties()));
//        rabbitTemplate.handleConfirm(new PendingConfirm());
//    }

}
