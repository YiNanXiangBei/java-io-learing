package threads;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date created in 上午10:46 18-9-18
 */
public class NIOServer extends AbstractServer {
    private static ThreadPoolExecutor threadPoolExecutor;

    public NIOServer(int port) {
        super(port);
    }

    @Override
    public void start() {
//        new Thread(new Acceptor(selector, serverSocketChannel)).start();
        Acceptor acceptor = new Acceptor(selector, serverSocketChannel);

        threadPoolExecutor.execute(acceptor);
    }

    @Override
    public void close() {

        super.close();
    }

    public static void main(String[] args) {
        threadPoolExecutor = new ThreadPoolExecutor(4, 10, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String threadName = NIOServer.class.getSimpleName() + count.addAndGet(1);
                thread.setName(threadName);
                return thread;
            }
        });
        NIOServer server = new NIOServer(7071);
        server.start();
    }
}
