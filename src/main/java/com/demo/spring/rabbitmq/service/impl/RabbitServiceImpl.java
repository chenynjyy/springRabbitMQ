package com.demo.spring.rabbitmq.service.impl;

import com.demo.spring.rabbitmq.service.RabbitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chenyunan on 2017/7/19.
 */
@Service
public class RabbitServiceImpl implements RabbitService {

    Logger logger = LoggerFactory.getLogger(RabbitServiceImpl.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send(String exchange, String routingKey, Object message) {

        Object recvMessage = rabbitTemplate.convertSendAndReceive(exchange, routingKey, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties messageProperties = message.getMessageProperties();
                return message;
            }
        });
        logger.info("something : " + new String((byte[]) recvMessage));

    }

}
