package com.hansolinticube.gateway.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.text.MessageFormat;

public class NettyClient {
    final private String host;
    final private int port;
    final private boolean isPrimary;  // 해당 클라이언트가 Primary 서버에 접속할 때 true, Backup 서버에 접속할 때 false

    private Channel serverChannel;
    private EventLoopGroup eventLoopGroup;

    private String dataFromCTIBridge = null;
    private final int packetLength;

    public NettyClient(String host, int port, boolean isPrimary, int packetLength) {
        this.host = host;  // CTIBridge 아이피
        this.port = port;  // CTUBridge 리스닝 포트
        this.isPrimary = isPrimary;  // 연결 대상이 Primary 서버인지 확인 여부
        this.packetLength = packetLength;
    }

    /**
     * CTIBridge 서버로 연결
     * NettyInitializer 클래스의 생성자를 호출할 때 packetLength 값을 전달하여 네트워크 통신에 필요한 초기화 작업을 수행한다.
     * packetLength는 패킷의 길이를 지정하며, initChannel 메서드 내에서 해당 값에 기반하여 특정 핸들러를 추가하고 초기화한다.
     */
    public void connect() throws Exception {
        eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("client"));

        Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(new InetSocketAddress(host, port));
        bootstrap.handler(new NettyInitializer(this, packetLength));
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);  // 연결 타임아웃 10초

        try {
            serverChannel = bootstrap.connect().sync().channel();
        } catch (InterruptedException e) {
            throw new Exception(MessageFormat.format("CTIBridge와의 연결을 시도하였으나 에러가 발생하였습니다.({0})", e.getMessage()));
        }
    }

    /**
     * CTIBridge 서버로 데이터 전송
     */
    public void sendData(String message) {
        ChannelFuture future  = serverChannel.writeAndFlush(message);
    }

    /**
     * CTIBridge 서버와의 연결 종료
     */
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    public boolean getSessionStatus() {
        return serverChannel.isOpen();
    }

    public String getDataFromCTIBridge() {
        return this.dataFromCTIBridge;
    }

    public void setDataFromCTIBridge(String data) {
        this.dataFromCTIBridge = data;
    }

    public boolean isPrimary() {
        return this.isPrimary;
    }

}
