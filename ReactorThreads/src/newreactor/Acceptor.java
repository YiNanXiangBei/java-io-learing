package newreactor;

import reactor.MultiReactor;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date created in 下午2:39 18-9-17
 */
public class Acceptor {

    public static void main(String[] args) throws Exception {

        System.out.println("Server Started at port : " + 7071);

        ThreadPoolExecutor reactors = new ThreadPoolExecutor(3, 6, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String threadName = MultiReactor.class.getSimpleName() + count.addAndGet(1);
                thread.setName("main " + threadName);
                return thread;
            }
        });

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(7071));
        serverSocketChannel.configureBlocking(false);

        Reactor reactor = new Reactor();
        reactor.registerChannel(SelectionKey.OP_ACCEPT, serverSocketChannel);
        reactor.run();
//        reactors.execute(reactor);
    }

}
