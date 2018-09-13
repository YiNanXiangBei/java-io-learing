import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author yinan
 * @date 18-9-12
 */
public class WriteEventHandler implements EventHandler {

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("====== Write Event Handler ======");
        SocketChannel socketChannel = (SocketChannel) handle.channel();

        System.out.print("输入的数据：");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        ByteBuffer byteBuffer = ByteBuffer.wrap(bufferedReader.readLine().
                getBytes(StandardCharsets.UTF_8));

        socketChannel.write(byteBuffer);
        handle.interestOps(SelectionKey.OP_READ);
    }
}
