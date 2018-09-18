import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author yinan
 * @date 18-9-18
 */
public class AIOServerHandler implements CompletionHandler<AsynchronousSocketChannel, AIOServer> {
    @Override
    public void completed(AsynchronousSocketChannel result, AIOServer attachment) {
        //处理下一次客户端请求，类似递归逻辑
        attachment.getServerSocketChannel().accept(attachment, this);
        doRead(result);
    }

    @Override
    public void failed(Throwable exc, AIOServer attachment) {
        exc.printStackTrace();
    }

    private void doRead(final AsynchronousSocketChannel channel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                System.out.println(attachment.capacity());
                attachment.flip();
                System.out.println("from client : " + new String(attachment.array(), StandardCharsets.UTF_8));

            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
    }

    private void doWrite(AsynchronousSocketChannel result) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println("enter message send to client > ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        buffer.put(line.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        result.write(buffer);
    }
}
