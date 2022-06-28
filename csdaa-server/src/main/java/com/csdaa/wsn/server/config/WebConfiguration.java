package com.csdaa.wsn.server.config;


import com.csdaa.wsn.server.handler.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private TokenInterceptor tokenInterceptor = new TokenInterceptor();

//    //跨域处理
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedHeaders("*")
//                .allowedMethods("*")
//                .allowedOrigins("*")
//                .allowCredentials(true);
//    }

    //异步请求
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3)));
        configurer.setDefaultTimeout(30000);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        //排除拦截，除了注册登录(此时还没token)，其他都拦截
        excludePath.add("/user/**");  //注册
        excludePath.add("/user/addUser");  //注册
        excludePath.add("/login/**");     //登录
        excludePath.add("/audit/doproof");
        excludePath.add("/audit/doverify");
        excludePath.add("/audit/dobatchproof");
        excludePath.add("/audit/dobatchverify");
        excludePath.add("/static/**");  //静态资源
        excludePath.add("/assets/**");  //静态资源
        excludePath.add("/category/**");
        excludePath.add("/goods/**");
        excludePath.add("/comment/comsbygoodid");
        excludePath.add("/comment/replysbycomid");
        excludePath.add("/seckill/getAllInfo");
        excludePath.add("/doc.html");
        excludePath.add("/doc.html#/**");
        excludePath.add("/webjars/**");
        excludePath.add("/swagger-resources/**");
        excludePath.add("/v3/**");
        excludePath.add("/trade/**");
        excludePath.add("/ttt/**");


        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
        WebMvcConfigurer.super.addInterceptors(registry);

    }
}