package com.hansolinticube.gateway.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class IOExceptionHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = LogManager.getLogger(IOExceptionHandler.class);
    
    /* IOException 오류를 처리하는 로직을 구현 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            logger.error("IOException 오류 발생", cause);

            // 연결을 닫거나 다른 처리를 수행할 수 있음
            ctx.close();
        } else {
            // IOException이 아닌 다른 유형의 예외는 다른 핸들러로 전달
            super.exceptionCaught(ctx, cause);
        }
    }
}
