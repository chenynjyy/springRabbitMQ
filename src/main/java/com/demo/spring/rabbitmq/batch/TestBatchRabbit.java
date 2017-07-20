package com.demo.spring.rabbitmq.batch;

import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.support.SimpleBatchingStrategy;
import org.springframework.jndi.JndiTemplate;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;

/**
 * Created by chenyunan on 2017/7/20.
 */
public class TestBatchRabbit {

    public void batch() {
        SimpleBatchingStrategy strategy = new SimpleBatchingStrategy(10, 100, 100);
//        strategy.
        DefaultManagedTaskScheduler scheduler = new DefaultManagedTaskScheduler();
        JndiTemplate jndiTemplate = new JndiTemplate();
//        jndiTemplate.
//        scheduler.setJndiTemplate();
        BatchingRabbitTemplate template  = new BatchingRabbitTemplate(strategy, scheduler);
    }

}
