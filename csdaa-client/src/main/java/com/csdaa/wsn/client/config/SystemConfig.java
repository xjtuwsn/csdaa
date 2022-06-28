package com.csdaa.wsn.client.config;

import com.csdaa.wsn.commons.entity.SysParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SystemConfig {
    @Bean
    public SysParam getSysParam(){

        return new SysParam();
    }
    @Bean
    public ExecutorService getService(){
        return Executors.newFixedThreadPool(17);
    }
}
