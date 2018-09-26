package com.yinan.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author yinan
 * @date 18-9-19
 */
public class Client {

    public static void main(String[] args) {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888).sync();
            Channel channel = channelFuture.channel();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                channel.writeAndFlush(bufferedReader.readLine()+"\r\n");
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }

}
