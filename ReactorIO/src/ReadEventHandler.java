import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author yinan
 * @date 18-9-12
 */
public class ReadEventHandler implements EventHandler {

    private Selector demultiplexer;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(2048);

//    public ReadEventHandler(Selector demultiplexer) {
//        this.demultiplexer = demultiplexer;
//    }

    public void setDemultiplexer(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {

        System.out.println("====== Read Event Handler ======");
        SocketChannel socketChannel = (SocketChannel) handle.channel();

        //从客户端读取数据
        socketChannel.read(inputBuffer);
        inputBuffer.flip();

        System.out.println("Received message  : " + new String(inputBuffer.array()));

        handle.interestOps(SelectionKey.OP_WRITE);

    }
}
