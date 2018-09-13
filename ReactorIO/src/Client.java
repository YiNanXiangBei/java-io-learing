import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author yinan
 * @date 18-9-12
 */
public class Client {

    public static void main(String[] args) throws Exception {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("127.0.0.1", 8881));
        Reactor reactor = new Reactor();
        //注册handler
        reactor.registerEventHandler(SelectionKey.OP_CONNECT, new ConnectEventHandler());
        reactor.registerEventHandler(SelectionKey.OP_READ, new ReadEventHandler());
        reactor.registerEventHandler(SelectionKey.OP_WRITE, new WriteEventHandler());
        //注册channel
        reactor.registerChannel(SelectionKey.OP_CONNECT
                | SelectionKey.OP_WRITE, channel);
        reactor.run();

    }

}
