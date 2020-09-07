package com.alun.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class Sender {

    @Autowired
    RabbitTemplate templatet;

    public void send() {
        //消息入库
        Email email = new Email();
        email.setStats("0");
        email.setSendTime(LocalDateTime.now());
        //id 全局唯一
        Message correlationData = new Message(UUID.randomUUID().toString(), email);
        templatet.convertAndSend("mail.exchange", "mail.routing.key", email, correlationData);
    }

    @Async
    public void sendAsync() {
        log.error("异步线程{}",Thread.currentThread().getName());
        //消息入库
        Email email = new Email();
        email.setStats("0");
        email.setSendTime(LocalDateTime.now());
        //id 全局唯一
        Message correlationData = new Message(UUID.randomUUID().toString(), email);
        templatet.convertAndSend("mail.exchange", "mail.routing.key", email, correlationData);
    }
}
