package com.demo.spring.rabbitmq.listen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Created by chenyunan on 2017/7/19.
 */
@RabbitListener(id = "testlistener",
        containerFactory = "containerFactory",
        priority = "1",
        bindings = {@QueueBinding(value = @Queue(value = "test.queue.1",
                                            durable = "true",
                                            exclusive = "false",
                                            autoDelete = "false",
                                            ignoreDeclarationExceptions = "false",
                                            arguments = {}),
                        exchange = @Exchange(value = "test.exchange.1",
                                durable = "true",
                                autoDelete = "false",
                                internal = "false",
                                ignoreDeclarationExceptions = "false",
                                delayed = "false",
                                arguments = {}),
                        key = "",
                        ignoreDeclarationExceptions = "false",
                        arguments = {})})
//@SendTo
public class TestListener {

    Logger logger = LoggerFactory.getLogger(TestListener.class);

    @RabbitHandler
    public String a(@Payload String a) {
        System.out.println(a);
        return a;
    }


}
