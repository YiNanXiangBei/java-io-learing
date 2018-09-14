package reactor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author yinan
 * @date created in 上午10:08 18-9-14
 */
public class BasicHandler implements Runnable {

    public static String LOG_PROMPT = "BasicHandler";

    private static final int MAXIN = 1024;
    private static final int MAXOUT = 1024;

    private SocketChannel socketChannel;

    private SelectionKey selectionKey;

    ByteBuffer input = ByteBuffer.allocate(MAXIN);
    ByteBuffer output = ByteBuffer.allocate(MAXOUT);

    final static int READING = 0, SENDING = 1;

    int state = READING;

    public BasicHandler(Selector selector, SocketChannel channel) throws IOException {
        this.socketChannel = channel;
        socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();

        System.out.println(LOG_PROMPT + ": Register OP_READ " +
                channel.socket().getLocalSocketAddress() + " and wakeup Selector");
    }

    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * 从通道读取字符串，需要判断特殊条件下字符串是否读取完毕
     * @throws IOException
     */
    protected void read() throws IOException {
        socketChannel.read(input);
        input.flip();
        Charset charset = Charset.forName("utf-8");
        String receiveData = charset.decode(input).toString();
        System.out.println(LOG_PROMPT + ": start reading ...");
        System.out.println(LOG_PROMPT + ": received " + receiveData);
        input.clear();
        state = SENDING;
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }


    //业务处理
//    protected void process() {
//        response.append("Echo > ").append(request.toString());
//        output.put(response.append("\n").toString().getBytes(StandardCharsets.UTF_8));
//        //清空缓冲区继续接收
//        input.clear();
//        request.delete(0, request.length());
//        System.out.println(LOG_PROMPT + ": Response - [" + response.toString() + "]");
//    }

    protected void send() throws IOException {
//        //获取到了数据
//        output = (ByteBuffer) selectionKey.attachment();
//        //切换模式进行数据输出
//        output.flip();
//        System.out.println(LOG_PROMPT + ": Received message: " + new String(output.array()));
//        //清空
//        output.clear();
        System.out.println(LOG_PROMPT + ": Begin to send message ...");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(LOG_PROMPT + ": Input : ");
        output = ByteBuffer.wrap(bufferedReader.readLine().
                getBytes(StandardCharsets.UTF_8));
        socketChannel.write(output);
        state = READING;
        selectionKey.interestOps(SelectionKey.OP_READ);
        output.clear();
        System.out.println(LOG_PROMPT + ": Continue reading ...");
    }

}
