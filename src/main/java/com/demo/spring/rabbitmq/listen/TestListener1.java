package com.demo.spring.rabbitmq.listen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * Created by chenyunan on 2017/7/19.
 */
@Component
@RabbitListener
public class TestListener1 {

    Logger logger = LoggerFactory.getLogger(TestListener1.class);

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = {@QueueBinding(
                    value = @Queue(value = "test.queue.1", durable = "true"),
                    exchange = @Exchange(
                            value = "test.exchange.1", durable = "true"),
                    key = "test.exchange.routing.key.1")})
//    @RabbitHandler
    @SendTo("amq.rabbitmq.reply-to")
    public void test(@Payload Message message, @Header(required = false) String haha) throws Exception {
        logger.info(new String(message.getBody()));
        MessageProperties messageProperties = message.getMessageProperties();
//        messageProperties.setHeader("haha", "hehe");
//        throw new Exception("haha");
//        messageProperties.setReplyTo("spring.test.message.reply.routing.key");
//        messageProperties.setReceivedExchange("spring.test.message.reply.exchange");
//        return "";
    }

//    @RabbitListener(bindings = {
//            @QueueBinding(
//                    value = @Queue(value = "test.reply", durable = "true"),
//                    exchange = @Exchange(value = "amq.rabbitmq.reply-to", durable = "false"),
//                    key = "test.exchange.routing.key.1"
//            )})
    public void test1(@Payload Message message) {
        logger.info(new String(message.getBody()));
    }

}
