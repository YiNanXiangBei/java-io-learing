package newreactor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

/**
 * @author yinan
 * @date created in 下午2:43 18-9-17
 */
public class WriteEventHandler extends AbstractEventHandler {

    private SelectionKey key;

    public WriteEventHandler(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void run() {
        Semaphore semaphore = new Semaphore(1);
        try {
            semaphore.acquire();
            handleEvent(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    /**
     * 针对写事件的处理
     * @param handle
     * @throws Exception
     */
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
