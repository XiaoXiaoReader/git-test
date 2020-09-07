package com.alun.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class EmailController {


    @Autowired
    Sender sender;

    @GetMapping("/mq")
    public void mq2() {
        sender.send();
    }

    @GetMapping("/mqasync")
    public void ma1() {
        log.error("主线程{}",Thread.currentThread().getName());
        sender.sendAsync();
    }
}
