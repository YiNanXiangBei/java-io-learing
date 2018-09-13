import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author yinan
 * @date 18-9-11
 */
public class NIOClient {
    private static Selector selector;
    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8881));
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            NIOClient client = new NIOClient();
            //selector.select() 是同步阻塞的
            while (selector.select() > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isConnectable()) {
                        client.connect(key);
                    } else if (key.isReadable()) {
                        client.receive(key);
                    } else if (key.isWritable()) {
                        client.write(key);
                    }
                }
            }
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理连接事件
     * @param key
     * @throws IOException
     */
    private void connect(SelectionKey key) throws IOException {
        //获取事件句柄对应的channel
        SocketChannel channel = (SocketChannel) key.channel();
        //完成真正的socket连接
        channel.finishConnect();
        //打印连接信息
        InetSocketAddress remote = (InetSocketAddress) channel.socket().getRemoteSocketAddress();
        String host = remote.getHostName();
        int port = remote.getPort();
        System.out.println(String.format("访问地址： %s:%s 连接成功!", host, port));

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
        System.out.println(String.format("服务端返回信息： %s",  receiveData));
    }

    private static String compositeRequest(String host){

        return "GET / HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "User-Agent: curl/7.43.0\r\n" +
                "Accept: */*\r\n\r\n";
//        return "Hello world!";
    }


}
