package com.hansolinticube.gateway.netty;

import com.hansolinticube.gateway.service.SelectCTILogService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyHandler extends SimpleChannelInboundHandler {
    NettyClient nettyClient;

    public NettyHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    /**
     * CTIBridge로부터 데이터를 수신했을 때 처리 로직
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        this.nettyClient.setDataFromCTIBridge((String)msg);
        this.nettyClient.close();
    }

}
