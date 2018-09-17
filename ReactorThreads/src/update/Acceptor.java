package update;

import reactor.MultiReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date 18-9-17
 */
public class Acceptor {

    public static void main(String[] args) throws IOException {

        System.out.println("Server Started at port : " + 7071);

        ThreadPoolExecutor reactors = new ThreadPoolExecutor(3, 4, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String threadName = MultiReactor.class.getSimpleName() + count.addAndGet(1);
                thread.setName("main " + threadName);
                return thread;
            }
        });

        ThreadPoolExecutor subReactors = new ThreadPoolExecutor(4, 5, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String threadName = MultiReactor.class.getSimpleName() + count.addAndGet(1);
                thread.setName("sub " + threadName);
                return thread;
            }
        });

        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(7071));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT );
        reactors.execute(new MainReactor(selector));
        subReactors.execute(new SubReactor(selector));

    }

}
