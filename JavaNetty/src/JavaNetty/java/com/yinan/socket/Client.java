package com.yinan.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date 18-9-19
 */
public class Client {

    public static void main(String[] args) {

        for (int i = 0; i < 2000; i++) {
            new Thread(new StartClient()).start();
            System.out.println("当前人数：" + i);
        }
//        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//
//        try {
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(eventLoopGroup)
//                    .channel(NioSocketChannel.class)
//                    .handler(new ClientInitializer());
//            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888).sync();
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//
//            while (true) {
//                channelFuture.channel().writeAndFlush(bufferedReader.readLine() + "\n");
//            }
//
//
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//        } finally {
//            eventLoopGroup.shutdownGracefully();
//        }

    }

}

class StartClient implements Runnable {

    @Override
    public void run() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888).sync();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                channelFuture.channel().writeAndFlush(bufferedReader.readLine() + "\n");
            }


        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }
}