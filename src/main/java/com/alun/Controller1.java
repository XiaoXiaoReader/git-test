package com.alun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class Controller1 {

    @Autowired
    CacheService cacheService;


    @GetMapping("get")
    public Object test(String key) {
        System.out.println(Thread.currentThread().getName()+"主刷新");
        try {
            return cacheService.test("aa");
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
