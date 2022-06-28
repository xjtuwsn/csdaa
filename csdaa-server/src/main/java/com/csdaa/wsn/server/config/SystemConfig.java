package com.csdaa.wsn.server.config;

import com.csdaa.wsn.commons.entity.SysParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SystemConfig {
    public static int threadNum=16;
    @Bean
    public SysParam getSysParam(){
        return new SysParam();
    }
    @Bean
    public ExecutorService getExecutor(){
        return Executors.newFixedThreadPool(threadNum);
    }
}
