import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date created in 上午10:20 18-9-26
 */
public class SysCompletableFutureDemo {

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        CompletableFuture.supplyAsync(() -> {
            System.out.println("在回调中执行耗时操作 ...");
            timeConsumingOperation();
            return 100;
        })
        .thenCompose(i -> CompletableFuture.supplyAsync(() -> {
            System.out.println("在回调中执行耗时操作 ...");
            timeConsumingOperation();
            return i + 100;
        }))
        .whenComplete((result, e) -> {
            System.out.println("计算结果： " + result);
        });
        System.out.println("主线程运算耗时: " + (System.currentTimeMillis() -l) + " ms");
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
