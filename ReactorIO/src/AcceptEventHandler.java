import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author yinan
 * @date 18-9-12
 */
public class AcceptEventHandler implements EventHandler {

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("====== Accept Event Handler ======");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) handle.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            socketChannel.configureBlocking(false);
            handle.interestOps(SelectionKey.OP_READ);
        }
    }
}
