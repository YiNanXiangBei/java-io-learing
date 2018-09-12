import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author yinan
 * @date 18-9-11
 */
public class NIOClient {
    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            boolean flag = socketChannel.connect(new InetSocketAddress("127.0.0.1", 8888));
            String request = "hello world";
            System.out.println("发送： " + request);
            byte[] bytes = request.getBytes(StandardCharsets.UTF_8);
            ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);
            socketChannel.write(writeBuffer);
            ByteBuffer buffer = ByteBuffer.allocate(100);
            socketChannel.read(buffer);
            byte[] data = buffer.array();
            String message = new String(data);
            System.out.println(message);
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
