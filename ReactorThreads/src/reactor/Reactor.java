package reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

/**
 * @author yinan
 * @date created in 下午3:40 18-9-14
 */
public class Reactor implements Runnable {

    //控制从reactor注册通道
    final Semaphore semaphore = new Semaphore(1);

    private Selector selector;

    public Reactor() throws IOException {
        selector = Selector.open();
    }

    public Selector getSelector() {
        return selector;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                //阻塞直到有通道事件就绪
                selector.select();
                //是否有通道在注册，有则阻塞，无则运行
                semaphore.acquire();
                //拿到就绪通道的集合
                Iterator<SelectionKey> selectKeys = selector.selectedKeys().iterator();
                while (selectKeys.hasNext()) {
                    SelectionKey selectionKey = selectKeys.next();
                    dispatch(selectionKey);
                }
                selectKeys.remove();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }

    private void dispatch(SelectionKey key) {
        Runnable runnable = (Runnable) key.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}
