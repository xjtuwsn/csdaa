package com.csdaa.wsn.server.controller;

import com.csdaa.wsn.server.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/audit")
public class AuditController {
    @Autowired
    AuditService auditService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @RequestMapping("/publishChall")
    public byte[] publishChallen(HttpServletRequest request){
        return auditService.publishChall(request);
    }
    @RequestMapping("/doproof")
    public String doProof(HttpServletRequest request){
        return auditService.doProof(request);
    }@RequestMapping("/doverify")
    public String doVerify(HttpServletRequest request){
        return auditService.doVerify(request);
    }

    @RequestMapping("/batchPublish")
    public byte[] batchPublish(HttpServletRequest request){
        return auditService.doBatchPublishChallen(request);
    }
    @RequestMapping("/dobatchproof")
    public String doBatchProof(HttpServletRequest request) throws Exception {
        return auditService.doBatchProof(request);
    }
    @RequestMapping("/dobatchverify")
    public String doBatchVerify(HttpServletRequest request) throws Exception {
        return auditService.doBatchVerify(request);
    }
    @RequestMapping("/isfin")
    public byte[] isAuditFin(HttpServletRequest request){
        return auditService.isAuditFin(request);
    }
    @RequestMapping("/test")
    public void test(){
       stringRedisTemplate.opsForSet().add("abc", "1","2","3","4");
        //Set<String> aba = stringRedisTemplate.opsForSet().members("aba");
        //Set<String> abc = stringRedisTemplate.opsForSet().members("abc");
        List<String> list=new ArrayList<>();
        list.add("3");list.add("5");list.add("6");
        String[] strings = list.toArray(new String[0]);
        stringRedisTemplate.opsForSet().add("abb", strings);
        Set<String> aba = stringRedisTemplate.opsForSet().members("abb");
        System.out.println(aba.size());
        //stringRedisTemplate.expire("abc",60, TimeUnit.SECONDS);
        Long abc1 = stringRedisTemplate.getExpire("abc");
        System.out.println(abc1);
        //System.out.println(aba.size());
        //System.out.println(abc.size());
    }
}
