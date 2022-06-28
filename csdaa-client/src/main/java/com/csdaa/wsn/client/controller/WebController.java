package com.csdaa.wsn.client.controller;

import com.alibaba.fastjson.JSONObject;
import com.csdaa.wsn.client.service.WebService;
import com.csdaa.wsn.commons.entity.LocalFile;
import io.netty.handler.codec.http.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/web")
public class WebController {

    @Autowired
    WebService webService;
    @Autowired
    ExecutorService service;
    @RequestMapping("/allFiles")
    public List<LocalFile> getAllFiles(){
        return webService.getAllFiles();
    }

    @RequestMapping("/query")
    public JSONObject query(String id){
        return webService.query(id);
    }
    @RequestMapping("/islogin")
    public boolean isLogin(){
        return webService.isLogin();
    }
    @RequestMapping("/login")
    public String login(String username,String password){
        //return webService.login(username,password);
        return webService.login(username,password);
    }
    @RequestMapping("/register")
    public String register(String username,String password,String flag){
        return webService.register(username,password,flag);
    }
    public String juc() throws ExecutionException, InterruptedException {
        List<Future<Integer>> list=new ArrayList<>();
        int theardNum=16,cnt=10000;
        for (int i = 0; i < theardNum; i++) {
            Future<Integer> submit = service.submit(new MyRunnable(i*cnt, (i+1) * cnt));
            list.add(submit);
        }
        int sum=0;
        long start = System.currentTimeMillis();
        for (int i = 1; i <= list.size(); i++) {
            Future<Integer> future = list.get(i-1);
            while (!future.isDone()) {}
            sum+=future.get();
        }
        long mid = System.currentTimeMillis();
        return mid-start+"";
    }
    @RequestMapping("/cctest")
    public String notjuc(HttpServletRequest request){
        Enumeration<String> headerNames = request.getHeaderNames();
        String qid = request.getParameter("qid");
        System.out.println(qid);
        Iterator<String> stringIterator = headerNames.asIterator();
        while (stringIterator.hasNext()) System.out.println(stringIterator.next());
        System.out.println();
        return "aaa";
    }

}
