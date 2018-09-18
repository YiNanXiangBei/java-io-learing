package threads;


import java.nio.channels.SocketChannel;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date created in 上午10:31 18-9-18
 */
public class WorkerThreadGroup {
    //负载均衡
    private static final AtomicInteger automic = new AtomicInteger();
    private WorkerThread[] workerThreads;
    private ThreadPoolExecutor poolExecutor;

    public WorkerThreadGroup() {
        this(Constants.DEFAULT_WORKER_THREAD_COUNT);
    }

    public WorkerThreadGroup(int threadCount) {
        workerThreads = new WorkerThread[threadCount];
        poolExecutor = new ThreadPoolExecutor(Constants.DEFAULT_WORKER_THREAD_COUNT, 10, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String threadName = WorkerThreadGroup.class.getSimpleName() + count.addAndGet(1);
                thread.setName(threadName);
                return thread;
            }
        });
        for (int i = 0; i < threadCount; i++) {
            workerThreads[i] = new WorkerThread();
            poolExecutor.execute(workerThreads[i]);
//            new Thread(workerThreads[i]).start();
        }
    }

    //添加channel到相关线程下的任务队列中
    public void dispatch(SocketChannel socketChannel) {
        if (socketChannel != null) {
            this.workerThreads[ automic.getAndIncrement() % workerThreads.length ].register(socketChannel);
        }
    }
}
