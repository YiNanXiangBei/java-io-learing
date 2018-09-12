import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author yinan
 * @date 18-9-12
 */
public class WriteEventHandler implements EventHandler {

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        SocketChannel socketChannel = (SocketChannel) handle.channel();

        ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();
        socketChannel.write(inputBuffer);
        handle.interestOps(SelectionKey.OP_READ);
//        socketChannel.close();
    }
}
