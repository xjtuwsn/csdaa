package com.csdaa.wsn.server.service;

import com.csdaa.wsn.server.config.TempSys;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @project:csdaa-server
 * @file:HttpClient
 * @author:wsn
 * @create:2022/6/10-19:21
 */
@Service
public class HttpClient {
    public String call(String url, HttpMethod method, MultiValueMap<String,String> para){
        RestTemplate template=new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String compURL= TempSys.OTHER_SSP_HOST+":"+TempSys.OTHER_SSP_PORT+url;
        HttpEntity<MultiValueMap<String,String>>requestEntity=new HttpEntity<>(para,headers);
        ResponseEntity<String> exchange = template.exchange(compURL, HttpMethod.POST, requestEntity, String.class);
        return exchange.getBody();
    }
}
