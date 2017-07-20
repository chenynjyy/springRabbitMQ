package com.demo.spring.rabbitmq.controller;

import com.demo.spring.rabbitmq.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chenyunan on 2017/7/19.
 */
@RestController
public class RabbitController {

    @Autowired
    RabbitService rabbitService;

    @RequestMapping(value = "name", produces = "application/json")
    public String name(@RequestParam String message) {
        rabbitService.send("test.exchange.1", "test.exchange.routing.key.1", message);
        return message;
    }

}
