package com.cloud.example.caller;


import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class HttpManager {
    public static final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://localhost:5050/demo/";
    private static final String WAIT_REQUEST_PATH = "/hello/wait";
    private static final String BUSINESS_KEY_REQUEST_PATH = "/hello/getName/nico";
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread waitBusiness = new Thread(new BootTask(countDownLatch, WAIT_REQUEST_PATH), "boot-wait-Thread");
        Thread keyBusiness = new Thread(new BootTask(countDownLatch, BUSINESS_KEY_REQUEST_PATH), "boot-key-Thread");
        waitBusiness.start();
        countDownLatch.countDown();
        keyBusiness.start();
        countDownLatch.countDown();
    }

    static class BootTask implements Runnable{
        private CountDownLatch countDownLatch;
        private String requestPath;

        public BootTask(CountDownLatch countDownLatch, String requestPath){
            this.countDownLatch = countDownLatch;
            this.requestPath = requestPath;
        }

        @Override
        public void run() {
            CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
            List<Thread> threads = new ArrayList<Thread>();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int k=0; k<10;k++){
                new Thread(new RunTask(cyclicBarrier, requestPath),"c"+k).start();
            }
        }
    }



    static class RunTask implements Runnable{
        private CyclicBarrier cyclicBarrier;
        private String requestPath;

        public RunTask(CyclicBarrier cyclicBarrier, String requestPath){
            this.cyclicBarrier = cyclicBarrier;
            this.requestPath = requestPath;
        }

        @Override
        public void run() {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "启动@time=" + System.currentTimeMillis());
            callRequest();
        }

        public  void callRequest(){
            int i =0;
            while (i<60){
                call();
                i++;
            }

        }
        public void call(){
            Request request = new Request.Builder()
                    .url(BASE_URL + requestPath)
                    .build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
