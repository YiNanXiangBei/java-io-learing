package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author yinan
 * @date created in 上午10:12 18-9-18
 */
public abstract class EventHandler {

    public void handler(Set<SelectionKey> ops, Selector selector) {
        for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
            SelectionKey key = it.next();
            it.remove();
            try {
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    //向选择器注册读事件， 客户端向服务端发送数据准备好后，再处理
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println(Thread.currentThread().getName() + " 收到客户端请求...");
                } else if (key.isWritable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();

                    System.out.print(Thread.currentThread().getName() + " 输入的数据：");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    ByteBuffer buffer = ByteBuffer.wrap(bufferedReader.readLine().
                            getBytes(StandardCharsets.UTF_8));

//                    ByteBuffer buffer = (ByteBuffer) key.attachment();
//                    buffer.flip();
                    clientChannel.write(buffer);
                    //重新注册读事件
                    clientChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(2048);
                    clientChannel.read(buffer);
                    buffer.flip();
                    Charset charset = Charset.forName("UTF-8");
                    String receivedData = charset.decode(buffer).toString();
                    System.out.println(Thread.currentThread().getName() + " Received message  : " + receivedData);
                    buffer.clear();
                    clientChannel.register(selector, SelectionKey.OP_WRITE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
