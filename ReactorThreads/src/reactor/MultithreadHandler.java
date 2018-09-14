package reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date created in 下午5:04 18-9-14
 */
public class MultithreadHandler extends BasicHandler {

    private static final int POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    static {
        LOG_PROMPT = "MultithreadHandler";
    }
    static final int PROCESSING = 3;
    private Object lock = new Object();
    private static ThreadPoolExecutor workPool = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, 10, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactory() {
        private AtomicInteger count = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread();
            String threadName = MultiReactor.class.getSimpleName() + count.addAndGet(1);
            thread.setName(threadName);
            return thread;
        }
    });

    public MultithreadHandler(Selector selector, SocketChannel channel) throws IOException {
        super(selector, channel);
    }

    @Override
    public void read() {
        synchronized (lock) {
            System.out.println(LOG_PROMPT + ": Start reading ... ");
        }
    }
}
