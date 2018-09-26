import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date created in 上午9:39 18-9-26
 */
public class NettyFutureDemo {

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        EventExecutorGroup group = new DefaultEventExecutorGroup(4);
        Future<Integer> future = group.submit(() -> {
            System.out.println("执行操作 ...");
            timeConsumingOperation();
            return 100;
        });

        future.addListener( future1 -> System.out.println("计算结果: " + future1.get()));

        System.out.println("主线程运算耗时： " + (System.currentTimeMillis() - l) + " ms");
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void timeConsumingOperation() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
