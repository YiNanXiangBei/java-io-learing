import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
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
                new InetSocketAddress("127.0.0.1", 8881)
        );
        //设置通道为非阻塞
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        //注册channel到selector
        serverSocketChannel.register(selector,  SelectionKey.OP_ACCEPT);
        while (selector.select() > 0) {
            //罗列所有的channel状态
            Iterator<SelectionKey> iterable = selector.selectedKeys().iterator();
            while (iterable.hasNext()) {
                SelectionKey key = iterable.next();
                iterable.remove();
                NIOServer server = new NIOServer();
                if (key.isAcceptable()) {
                    //获取满足条件的channel 即 OP_ACCEPT
                    ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
                    //服务端接收一个客户端连接
                    SocketChannel channel = socketChannel.accept();
                    channel.configureBlocking(false);
                    //注册这个客户端连接状态
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()){
                    server.receive(key);
                } else if (key.isWritable()) {
                    server.write(key);
                }
            }


        }

    }

    private static void receive1(SelectionKey key) {
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
            //切换状态，如果一次没有读完，接着读
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

    /**
     * 处理写入就绪事件
     * @param key
     * @throws IOException
     */
    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        InetSocketAddress remote = (InetSocketAddress) channel.socket().getRemoteSocketAddress();
        String host = remote.getHostName();
        Charset charset = Charset.forName("utf-8");
        //获取http请求数据
        String request = compositeRequest(host);
        //向SocketChannel中写入事件
        channel.write(charset.encode(request));
        //修改SocketChannel所关心的事件
        key.interestOps(SelectionKey.OP_READ);

    }

    private void receive(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        buffer.flip();
        Charset charset = Charset.forName("utf-8");
        String receiveData = charset.decode(buffer).toString();
        //当没有数据可读时，取消在选择器中关联，并关闭socket连接
        if ("".equals(receiveData)) {
            key.channel();
            channel.close();
            return;
        }
        System.out.println(String.format("客户端发送信息: %s", receiveData));
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static String compositeRequest(String host){

        return "明天，你好";
    }

}
