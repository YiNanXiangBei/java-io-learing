package threads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author yinan
 * @date created in 上午10:43 18-9-18
 */
public abstract class AbstractServer {

    protected String ip;

    protected int port;

    //reactor 中的selector
    protected Selector selector;

    protected ServerSocketChannel serverSocketChannel;

    public AbstractServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        init();
        start();
    }

    public AbstractServer(int port) {
        this.port = port;
        init();
        start();
    }


    private void init() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (selector.isOpen()) {
                selector.close();
            }

            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {}

}
