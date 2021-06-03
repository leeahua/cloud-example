package com.cloud.example.controller;


import com.cloud.example.hystrixcmd.CommandNormalBusinessThreadPool;
import com.cloud.example.hystrixcmd.CommandThreadPool;
import com.cloud.example.service.HelloService;
import com.cloud.example.service.WaitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("hello")
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);
    @Resource
    private HelloService helloService;

    @Resource
    private WaitService waitService;

    @GetMapping("/getName/{name}")
    public String getName(@PathVariable("name") String name){
        return new CommandThreadPool().setHelloService(helloService).setName(name).execute();
    }

    @GetMapping("/wait")
    public String  waitTime(){
        log.info("here!");
       return new CommandNormalBusinessThreadPool().setWaitService(waitService).execute();
    }
}
