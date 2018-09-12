import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;


/**
 * @author yinan
 * @date 18-9-12
 */
public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8881));
        serverSocketChannel.configureBlocking(false);
        Reactor reactor = new Reactor();
        //注册handler
        reactor.registerEventHandler(SelectionKey.OP_ACCEPT, new AcceptEventHandler());
        reactor.registerEventHandler(SelectionKey.OP_READ, new ReadEventHandler());
        reactor.registerEventHandler(SelectionKey.OP_WRITE, new WriteEventHandler());
        //注册channel
        reactor.registerChannel(SelectionKey.OP_ACCEPT, serverSocketChannel);
        reactor.run();
    }

}
