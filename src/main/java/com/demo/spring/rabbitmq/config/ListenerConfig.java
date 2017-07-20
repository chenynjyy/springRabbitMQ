package com.demo.spring.rabbitmq.config;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;

/**
 * Created by chenyunan on 2017/7/19.
 */
//@Configuration
//@EnableRabbit
public class ListenerConfig implements RabbitListenerConfigurer {

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
//        registrar.setContainerFactory();
    }

}
