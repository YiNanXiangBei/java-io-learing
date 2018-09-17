package newreactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Semaphore;

/**
 * @author yinan
 * @date created in 下午2:46 18-9-17
 */
public class AcceptEventHandler extends AbstractEventHandler {

    private SelectionKey key;

    private Selector demultiplexer;

    public AcceptEventHandler(SelectionKey key, Selector demultiplexer) {
        this.key = key;
        this.demultiplexer = demultiplexer;
    }

    @Override
    public void run() {
        Semaphore semaphore = new Semaphore(1);
        try {
            semaphore.acquire();
            handleEvent(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    /**
     * accept事件处理
     * @param handle
     * @throws Exception
     */
    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("===== Accept Event Handler =====");
        ServerSocketChannel serverSocketChannel =
                (ServerSocketChannel) handle.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            socketChannel.configureBlocking(false);
            socketChannel.register(
                    demultiplexer, SelectionKey.OP_READ);
        }
    }
}
