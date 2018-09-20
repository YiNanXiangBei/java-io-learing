package com.yinan.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author yinan
 * @date 18-9-20
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;

            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空间";
                    break;
                case WRITER_IDLE:
                    eventType = "写空间";
                    break;
                case ALL_IDLE:
                    eventType = "读写空间";
                default:
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "事件： " + eventType);

            ctx.channel().close();
        }
    }

}
