import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * @author yinan
 * @date 18-9-18
 */
public class AIOClient {

    private AsynchronousSocketChannel channel;

    public AIOClient(String host, int port) {
        init(host, port);
    }

    public void init(String host, int port) {
        try {
            //开启通道
            channel = AsynchronousSocketChannel.open();
            //发起请求
            channel.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String line) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.put(line.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        channel.write(buffer);
    }

    public void read() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            //read方法是异步方法，Os实现的。get方法是一个阻塞方法，会等待OS处理结束之后再返回
            channel.read(buffer).get();
            buffer.flip();
            System.out.println("from server :" + new String(buffer.array(), StandardCharsets.UTF_8));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void doDestory() {
        if (null != channel) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        AIOClient client = new AIOClient("localhost", 9999);
        try {
            System.out.println("enter message send to server >");
            Scanner s = new Scanner(System.in);
            String line  = s.nextLine();
            client.write(line);
            client.read();
        } finally {
            client.doDestory();
        }
    }

}
