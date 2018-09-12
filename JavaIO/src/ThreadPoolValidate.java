import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yinan
 * @date created in 上午10:09 18-9-12
 */
public class ThreadPoolValidate {
    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3, 4, 5, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger(0);
            @Override
            //生成线程，并对线程重命名
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                //                    TimeUnit.SECONDS.sleep(2);
                String threadName = ThreadPoolValidate.class.getSimpleName() + count.addAndGet(1);
//                    System.out.println(threadName + " run");
                thread.setName(threadName);
                return thread;
            }
        });
        Runnable runnable = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + " run");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        System.out.println("---先开三个---");
        System.out.println("核心线程数" + poolExecutor.getCorePoolSize());
        System.out.println("线程池数" + poolExecutor.getPoolSize());
        System.out.println("队列任务数" + poolExecutor.getQueue().size());
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        poolExecutor.execute(runnable);
        System.out.println("---再开三个---");
        System.out.println("核心线程数" + poolExecutor.getCorePoolSize());
        System.out.println("线程池数" + poolExecutor.getPoolSize());
        System.out.println("队列任务数" + poolExecutor.getQueue().size());
        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("----8秒之后----");
        System.out.println("核心线程数" + poolExecutor.getCorePoolSize());
        System.out.println("线程池数" + poolExecutor.getPoolSize());
        System.out.println("队列任务数" + poolExecutor.getQueue().size());

    }
}

class MyThread implements Runnable {

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2000);
            System.out.println(Thread.currentThread().getName() + " run");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

