import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author yinan
 * @date created in 上午9:23 18-9-14
 */
public class Reactor implements Runnable {

    public static void main(String[] args) {
        try {
            Thread thread = new Thread(new Reactor(10393));
            thread.setName("Reactor");
            thread.start();
            thread.join();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static final String LOG_PROMPT = "Reactor";

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        //选择器，为多个通道提供服务
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        //绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //添加一个对象
        sk.attach(new Acceptor());
        System.out.println(LOG_PROMPT + ": Listening on port " + port);

    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                //select()阻塞，等待有事件发生唤醒
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    String action = "";
                    if (sk.isAcceptable()) {
                        action = "OP_ACCEPT";
                    } else if(sk.isConnectable()) {
                        action = "OP_CONNECT";
                    } else if(sk.isReadable()) {
                        action = "OP_READ";
                    } else if(sk.isWritable()) {
                        action = "OP_WRITE";
                    }

                    System.out.println();
                    System.out.println(LOG_PROMPT + ": Action - " + action);

                    dispatch(sk);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) {
        //拿到注册时附加的对象
        Runnable r = (Runnable) (key.attachment());
        if (r != null)
            r.run();
    }

    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                //接收连接，没有连接直接返回null
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null) {
                    System.out.println(LOG_PROMPT + ": Accept and handler - " + channel.socket().getLocalSocketAddress());
                    //单线程处理连接
                    new BasicHandler(selector,channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
