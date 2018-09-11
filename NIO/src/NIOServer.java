import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @author yinan
 * @date 18-9-11
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
        //创建一个channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定端口
        serverSocketChannel.socket().bind(
                new InetSocketAddress("127.0.0.1", 8888)
        );
        //设置通道为非阻塞
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        //注册channel到selector
        serverSocketChannel.register(selector,  SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            //罗列所有的channel状态
            Iterator<SelectionKey> iterable = selector.selectedKeys().iterator();
            while (iterable.hasNext()) {
                SelectionKey key = iterable.next();
                iterable.remove();
                if (key.isAcceptable()) {
                    //获取满足条件的channel 即 OP_ACCEPT
                    ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel channel = socketChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()){
                    recive(key);
                }
            }


        }

    }

    private static void recive(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            ByteBuffer buffer =  ByteBuffer.allocate(5 * 1024);
            buffer.clear();
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            readBuffer.clear();
            while (true) {
                int bytesRead = channel.read(readBuffer);
                if (bytesRead > 0) {
                    readBuffer.flip();
                    buffer.put(readBuffer);
                    readBuffer.clear();
                } else {
                    break;
                }
            }
            buffer.flip();
            String request = new String(buffer.array());
            System.out.println(request);
            String response = "hellow world";
            ByteBuffer writeBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
            channel.write(writeBuffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
