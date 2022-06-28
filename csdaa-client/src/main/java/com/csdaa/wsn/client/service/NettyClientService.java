package com.csdaa.wsn.client.service;

import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.client.handler.HttpClient;
import com.csdaa.wsn.client.utils.SyncFuture;
import com.google.common.cache.*;
import io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Service
public class NettyClientService {
    private static LoadingCache<String, SyncFuture> futureCache = CacheBuilder.newBuilder()
            //设置缓存容器的初始容量为10
            .initialCapacity(1000)
            // maximumSize 设置缓存大小
            .maximumSize(100000)
            //设置并发级别为20，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(20)
            // expireAfterWrite设置写缓存后8秒钟过期
            .expireAfterWrite(12000, TimeUnit.SECONDS)
            //设置缓存的移除通知
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {

                }
            })
            //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            .build(new CacheLoader<String, SyncFuture>() {
                @Override
                public SyncFuture load(String key) throws Exception {
                    // 当获取key的缓存不存在时，不需要自动添加
                    return null;
                }
            });
    @Autowired
    public HttpClient httpClient;
    public ResponseMsg sendSyncMsg(String path, byte[]data, HttpMethod method,String dataId,String token) {

        SyncFuture<ResponseMsg> syncFuture = new SyncFuture<>();
        // 放入缓存中
        futureCache.put(dataId, syncFuture);

        // 封装数据

        // 发送同步消息
        ResponseMsg result = httpClient.sendMsgAsc(path,data,method,syncFuture,token);

        return result;
    }
    public void sendMsg(String path, byte[]data, HttpMethod method,String token){
        try {
            httpClient.sendMsg(path,data,method,token);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
    public void ackSyncMsg(ResponseMsg msg) {


        String dataId = msg.dataId;
        System.out.println("ackSyncMSG");
        // 从缓存中获取数据
        SyncFuture<ResponseMsg> syncFuture = futureCache.getIfPresent(dataId);

        // 如果不为null, 则通知返回
        if(syncFuture != null) {
            syncFuture.setResponse(msg);
            //主动释放
            futureCache.invalidate(dataId);
        }
    }
}
