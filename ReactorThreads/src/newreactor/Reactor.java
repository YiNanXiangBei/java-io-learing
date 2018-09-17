package newreactor;

import reactor.MultiReactor;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date created in 下午3:27 18-9-17
 */
public class Reactor implements Runnable {

    private Selector demultiplexer;

    private ThreadPoolExecutor subReactors = new ThreadPoolExecutor(6, 10, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
        private AtomicInteger count = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            String threadName = MultiReactor.class.getSimpleName() + count.addAndGet(1);
            thread.setName("sub " + threadName);
            return thread;
        }
    });

    public Reactor() throws Exception {
        this.demultiplexer = Selector.open();
    }

    public Selector getDemultiplexer() {
        return demultiplexer;
    }


    public void registerChannel(
            int eventType, SelectableChannel channel) throws Exception {
        channel.register(demultiplexer, eventType);
    }

    @Override
    public void run() {
        System.out.println("线程： " + Thread.currentThread().getName() + " 开始！");
        Semaphore semaphore = new Semaphore(1);
        while (!Thread.interrupted()) {
            try {
                semaphore.acquire();

                demultiplexer.select();

                Set<SelectionKey> readyHandles =
                        demultiplexer.selectedKeys();
                Iterator<SelectionKey> handleIterator =
                        readyHandles.iterator();

                while (handleIterator.hasNext()) {
                    SelectionKey handle = handleIterator.next();
                    if (handle.isAcceptable()) {
                        AbstractEventHandler handler =
                                new AcceptEventHandler(handle, demultiplexer);
                        subReactors.execute(handler);
                        handleIterator.remove();
                    }

                    if (handle.isReadable()) {
                        AbstractEventHandler handler =
                                new ReadEventHandler(handle);
                        subReactors.execute(handler);
                        handleIterator.remove();
                    }

                    if (handle.isWritable()) {
                        AbstractEventHandler handler =
                                new WriteEventHandler(handle);
                        subReactors.execute(handler);
                        handleIterator.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }

    }
}
