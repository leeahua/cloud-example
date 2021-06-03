package com.cloud.example.hystrixcmd;

import com.cloud.example.service.HelloService;
import com.cloud.example.service.WaitService;
import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandNormalBusinessThreadPool extends HystrixCommand<String> {
    private static final Logger log = LoggerFactory.getLogger(CommandNormalBusinessThreadPool.class);
    private String name;
    private WaitService waitService;

    public CommandNormalBusinessThreadPool() {
        super(setter());
    }

    private static Setter  setter() {
        //服务分组标识
        HystrixCommandGroupKey groupKey =
                HystrixCommandGroupKey.Factory.asKey("hello-wait-group-key");
        //服务标识
        HystrixCommandKey commandKey =
                HystrixCommandKey.Factory.asKey("hello-wait-command-key");
        //线程池名称
        HystrixThreadPoolKey threadPoolKey =
                HystrixThreadPoolKey.Factory.asKey("hello-wait-thread-pool-key");
        //线程池配置
        HystrixThreadPoolProperties.Setter threadPoolProperties =
                HystrixThreadPoolProperties.Setter()
                        .withCoreSize(1)
                        .withKeepAliveTimeMinutes(1)
                        .withMaxQueueSize(2)
                        .withQueueSizeRejectionThreshold(2);
        //命令属性配置
        HystrixCommandProperties.Setter commandProperties =
                HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);

        return Setter
                .withGroupKey(groupKey)
                .andCommandKey(commandKey)
                .andThreadPoolKey(threadPoolKey)
                .andThreadPoolPropertiesDefaults(threadPoolProperties)
                .andCommandPropertiesDefaults(commandProperties);
    }

    @Override
    protected String run() throws Exception {
        log.info("business-pool-name:{}, waiting", Thread.currentThread());

        log.info("business-pool-name:{}, wake up", Thread.currentThread());
        return waitService.waitTime();
    }

    @Override
    protected String getFallback(){
        return "Server is busy!";
    }

    public CommandNormalBusinessThreadPool setName(String name) {
        this.name = name;
        return this;
    }

    public CommandNormalBusinessThreadPool setWaitService(WaitService waitService) {
        this.waitService = waitService;
        return this;
    }
}
