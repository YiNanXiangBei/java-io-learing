package threads;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author yinan
 * @date created in 上午10:36 18-9-18
 */
public class Acceptor implements Runnable {

    private WorkerThreadGroup workerThreadGroup;

    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    public Acceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        workerThreadGroup = new WorkerThreadGroup();
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        Set<SelectionKey> ops = null;
        while (true) {
            try {
                selector.select();
                ops = selector.selectedKeys();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            for (Iterator<SelectionKey> iterator = ops.iterator(); iterator.hasNext();) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        System.out.println(Thread.currentThread().getName() + " 收到客户端的连接请求 ...");
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        workerThreadGroup.dispatch(socketChannel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
