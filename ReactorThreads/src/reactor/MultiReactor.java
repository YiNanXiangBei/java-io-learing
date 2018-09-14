package reactor;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date created in 下午3:11 18-9-14
 */
public class MultiReactor {

    public static void main(String[] args) throws IOException {
        MultiReactor reactor = new MultiReactor(10393);
        reactor.start();
    }

    //线程池代大小，其中一个线程被mainReactor占用，其余被subReactor占用
    private Reactor maineReactor;

    //用于处理I/O
    private Reactor[] reactors = new Reactor[POOL_SIZE - 1];

    int next = 0;
    private int port;
    private static final int POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, 10, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactory() {
        private AtomicInteger count = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread();
            String threadName = MultiReactor.class.getSimpleName() + count.addAndGet(1);
            thread.setName(threadName);
            return thread;
        }
    });

    public MultiReactor(int port) {
        try {
            this.port = port;
            maineReactor = new Reactor();
            for (int i = 0; i < reactors.length; i++) {
                reactors[i] = new Reactor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() throws IOException {
        Thread mThread = new Thread(maineReactor);
        //将channel注册到mainReactor
        new Acceptor(maineReactor.getSelector(), reactors, port);
        poolExecutor.execute(mThread);
        for (Reactor reactor : reactors) {
            Thread thread = new Thread(reactor);
            poolExecutor.execute(thread);
        }

    }


}
