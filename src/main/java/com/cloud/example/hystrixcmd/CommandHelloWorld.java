package com.cloud.example.hystrixcmd;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import rx.Observable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CommandHelloWorld extends HystrixCommand<String> {

    private final String name;

    protected CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("HelloGroup"));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        return "Hello " + name + "!";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String result = new CommandHelloWorld("Bob").execute();
        System.out.println(result);

        Future<String> futureResult = new CommandHelloWorld("async").queue();
        System.out.println(futureResult.get());
        Observable<String> observeResult = new CommandHelloWorld("observe").observe();
        System.out.println(observeResult.toBlocking().single());
    }
}
