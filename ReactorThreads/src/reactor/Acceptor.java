package reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author yinan
 * @date created in 下午3:39 18-9-14
 */
public class Acceptor implements Runnable{
    private  Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int next=0;
    private Reactor[] reactors;

    public Acceptor(Selector selector, Reactor[] reactors,  int port) throws IOException {
        this.selector = selector;
        this.reactors = reactors;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        key.attach(this);
        System.out.println("mainReactor-" + "Acceptor: Listening on port: " + port);
    }

    @Override
    public synchronized void run() {
        //非阻塞模式下，没有结果直接返回null
        SocketChannel socketChannel = null;
        try {
            socketChannel = serverSocketChannel.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socketChannel != null) {
            System.out.println("mainReactor-" + "Acceptor: " + socketChannel.socket().getLocalSocketAddress() +" 注册到 subReactor-" + next);
            Reactor subReactor = reactors[next];
            Selector suSelector = subReactor.getSelector();
            try {
                //首先占住资源
                subReactor.semaphore.acquire();
                //唤醒selector,释放publickeys的锁定，并阻塞等待资源
                suSelector.wakeup();
                // 将连接的通道注册到这个从 Reactor 上（这个 Handler 初始时调用了 wakeup）
                new BasicHandler(suSelector, socketChannel);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                //释放资源， 从reactor中获取到资源，继续后续处理
                subReactor.semaphore.release();
                // 当 从Reactor 处理完毕后，调用 select，会立马返回，处理新注册通道的读写
                suSelector.wakeup();
            }
            if(++next == reactors.length)
                next = 0;
        }
    }
}
