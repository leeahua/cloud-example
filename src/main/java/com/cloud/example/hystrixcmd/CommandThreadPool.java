package com.cloud.example.hystrixcmd;

import com.cloud.example.service.HelloService;
import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandThreadPool extends HystrixCommand<String> {
    private static final Logger log = LoggerFactory.getLogger(CommandThreadPool.class);
    private String name;
    private HelloService helloService;

    public  CommandThreadPool() {
        super(setter());
    }

    private static Setter  setter() {
        //服务分组标识
        HystrixCommandGroupKey groupKey =
                HystrixCommandGroupKey.Factory.asKey("hello-key-group-key");
        //服务标识
        HystrixCommandKey commandKey =
                HystrixCommandKey.Factory.asKey("hello-key-command-key");
        //线程池名称
        HystrixThreadPoolKey threadPoolKey =
                HystrixThreadPoolKey.Factory.asKey("hello-key-thread-pool-key");
        //线程池配置
        HystrixThreadPoolProperties.Setter threadPoolProperties =
                HystrixThreadPoolProperties.Setter()
                        .withCoreSize(10)
                        .withKeepAliveTimeMinutes(5)
                        .withMaxQueueSize(20000)
                        .withQueueSizeRejectionThreshold(20000);
        //命令属性配置
        HystrixCommandProperties.Setter commandProperties =
                HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);

        return HystrixCommand.Setter
                .withGroupKey(groupKey)
                .andCommandKey(commandKey)
                .andThreadPoolKey(threadPoolKey)
                .andThreadPoolPropertiesDefaults(threadPoolProperties)
                .andCommandPropertiesDefaults(commandProperties);
    }

    @Override
    protected String run() throws Exception {
        log.info("business-pool-name:{}", Thread.currentThread());
        return helloService.getName(name);
    }

    public CommandThreadPool setName(String name) {
        this.name = name;
        return this;
    }

    public CommandThreadPool setHelloService(HelloService helloService) {
        this.helloService = helloService;
        return this;
    }
}
