package com.cloud.example.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String getName(String name){
        int i=0;
        synchronized (this){
            while (i<100000000){
                i++;
            }
        }
        return name;
    }
}
