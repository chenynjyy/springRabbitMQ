package com.demo.spring.rabbitmq.service;

/**
 * Created by chenyunan on 2017/7/19.
 */
public interface RabbitService {

    public void send(String exchange, String routingKey, Object message);

}
