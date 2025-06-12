package com.hansolinticube.gateway.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyInitializer extends ChannelInitializer<SocketChannel> {
    private final NettyClient nettyClient;
    private final int packetLength;

    public NettyInitializer(NettyClient nettyClient, int packetLength) {
        this.nettyClient = nettyClient;
        this.packetLength = packetLength;
    }

    /**
     * netty 기본 설정
     * initChannel 메서드는 주어진 SocketChannel의 파이프라인에 여러 핸들러를 추가하여
     * 특정 프로토콜 또는 통신 규약에 맞게 데이터를 처리한다.
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new FixedLengthFrameDecoder(packetLength)); // 데이터를 고정된 길이의 프레임으로 자르는 역할(어떤 서비스든간에 142byte만 리턴해주기로 함)
        pipeline.addLast(new StringDecoder()); // 문자열을 디코딩하는 핸들러
        pipeline.addLast(new StringEncoder()); // 문자열을 인코딩하는 핸들러
        pipeline.addLast(new NettyHandler(this.nettyClient)); // 사용자 정의 핸들러
        pipeline.addLast(new IOExceptionHandler()); // 입출력 예외를 처리 핸들러
    }
}
