package com.yinan.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.UUID;

/**
 * @author yinan
 * @date 18-9-19
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

        Channel channel = channelHandlerContext.channel();
        channels.forEach(ch->{
            if(channel!=ch){
                ch.writeAndFlush(channel.remoteAddress()+"发送了:"+s+"\n");
            }else{
                ch.writeAndFlush("自己发送了:"+s+"\n");
            }
        });
        System.out.println(channelHandlerContext.channel().remoteAddress() + " , " + s);

        channelHandlerContext.writeAndFlush("from server : " + s);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush("from server:" + channel.remoteAddress() + " 加入\n");
        channels.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush("from server:" + channel.remoteAddress() + " 离开\n");
        //channelGroup.remove(channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush(channel.remoteAddress() + " 上线\n");
        System.out.println(channel.remoteAddress()+"上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"下线");
    }
}
