package com.csdaa.wsn.client.handler;

import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.commons.entity.Stu;
import com.csdaa.wsn.client.service.NettyClientService;
import com.csdaa.wsn.client.utils.ByteBufToBytes;
import com.csdaa.wsn.client.utils.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class HttpClientHandler extends ChannelInboundHandlerAdapter {
    private ByteBufToBytes reader;
    @Lazy
    @Autowired
    public NettyClientService nettyClientService;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.out.println("CONTENT_TYPE:"
                    + response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            System.out.println(HttpUtil.getContentLength(response));
            if (HttpUtil.isContentLengthSet(response)) {
                reader = new ByteBufToBytes(
                        (int) HttpUtil.getContentLength(response));
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();

            reader.reading(content);
            content.release();
            ResponseMsg responseMsg=null;
            if (reader.isEnd()) {
                byte[] bytes = reader.readFull();
                responseMsg = (ResponseMsg) SerializeUtil.doDeserialize(bytes);
                //System.out.println("Server said:" + responseMsg.dataId);
               //ctx.close();
            }
            nettyClientService.ackSyncMsg(responseMsg);
        }
    }
}
