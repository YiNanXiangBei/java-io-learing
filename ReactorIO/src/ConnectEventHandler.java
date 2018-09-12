import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author yinan
 * @date 18-9-12
 */
public class ConnectEventHandler implements EventHandler {
    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("====== Connect Event Handler ======");
        SocketChannel socketChannel = (SocketChannel) handle.channel();
        socketChannel.finishConnect();

        InetSocketAddress remote = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
        String host = remote.getHostName();
        int port = remote.getPort();
        System.out.println(String.format("访问地址： %s:%s 连接成功!", host, port));
    }
}
