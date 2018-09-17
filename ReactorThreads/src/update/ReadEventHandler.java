package update;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author yinan
 * @date 18-9-17
 */
public class ReadEventHandler implements IEventHandler {

    private SelectionKey key;

    public ReadEventHandler(SelectionKey key) {
        this.key = key;
    }

    private ByteBuffer inputBuffer = ByteBuffer.allocate(2048);

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("====== Read Event Handler =======");
        SocketChannel socketChannel = (SocketChannel) handle.channel();
        //从客户端读取数据

        socketChannel.read(inputBuffer);
        inputBuffer.flip();
        Charset charset = Charset.forName("UTF-8");
        String receivedData = charset.decode(inputBuffer).toString();
        System.out.println("Received message  : " + receivedData);
        inputBuffer.clear();

        handle.interestOps(SelectionKey.OP_WRITE);
        handle.selector().wakeup();
    }
}
