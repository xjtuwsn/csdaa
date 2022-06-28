package com.csdaa.wsn.server.controller;

import com.csdaa.wsn.server.service.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/ttt")
public class TestController {
    @Autowired
    HttpClient httpClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @RequestMapping("/test")
    public String doTest(){
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.add("qid","aaa");
        String call = httpClient.call("/web/cctest", HttpMethod.POST, params);
        return call;
    }
    @RequestMapping("/rtest")
    public String doRedisTetx(){
        stringRedisTemplate.opsForList().rightPush("abcz","12");
        stringRedisTemplate.opsForList().rightPush("abcz","121");
        stringRedisTemplate.opsForList().rightPush("abcz","1212");
        List<String> abc = stringRedisTemplate.opsForList().range("abcz", 0, -1);
        for(String s:abc) System.out.println(s);
        return "";
    }
}
