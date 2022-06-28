package com.csdaa.wsn.client.handler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.client.utils.SyncFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HttpClient {

    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private String host="47.93.113.79";
//    private String host="localhost";
    private int port=11111;
    private Channel channel;
    @Autowired
    public HttpClientHandler httpClientHandler;
    @PostConstruct
    public void connect() throws Exception {

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpObjectAggregator(131072));
                    ch.pipeline().addLast(httpClientHandler);
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            channel=f.channel();
            //f.channel().closeFuture().sync();
        }
        finally {
            //workerGroup.shutdownGracefully();
        }
    }
    private DefaultFullHttpRequest buildRequest(String path,byte[]data,HttpMethod method,String token) throws URISyntaxException {
        URI uri = new URI("http://"+host+":"+port+path);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_0, method, uri.toASCIIString(),
                Unpooled.wrappedBuffer(data));
        // 构建http请求
        request.headers().set(HttpHeaderNames.HOST, host+":"+port);
        request.headers().set(HttpHeaderNames.CONNECTION,
                HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                request.content().readableBytes());
        request.headers().set("messageType", "normal");
        request.headers().set("businessType", "testServerState");
        request.headers().set("token", token);
        return request;
    }

    public void sendMsg(String path, byte []data, HttpMethod method,String token) throws URISyntaxException {
        channel.writeAndFlush(buildRequest(path,data,method,token));

    }
    public ResponseMsg sendMsgAsc(String path,byte []data,HttpMethod method, SyncFuture<ResponseMsg> syncFuture,String token){
        ResponseMsg responseMsg=null;
        try {
            ChannelFuture channelFuture = channel.writeAndFlush(buildRequest(path, data, method,token));
            responseMsg=syncFuture.get(12000, TimeUnit.SECONDS);
        } catch (InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
        return responseMsg;
    }

}