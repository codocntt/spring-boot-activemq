package com.codoteck.activemq.producer;

import com.codoteck.activemq.producer.model.Order;
import com.codoteck.activemq.producer.service.OrderSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ActiveMQProducerApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ActiveMQProducerApplication.class, args);
    }

    private static Logger log = LoggerFactory.getLogger(ActiveMQProducerApplication.class);

    @Autowired
    private OrderSender orderSender;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("Spring Boot Embedded ActiveMQ Configuration Example");

        for (int i = 0; i < 5; i++) {
            Order myMessage = new Order(i + " - Sending JMS Message using Embedded activeMQ", new Date());
            orderSender.send(myMessage);
        }

        log.info("Waiting for all ActiveMQ JMS Messages to be consumed");
        TimeUnit.SECONDS.sleep(3);
        System.exit(-1);
    }

}
