import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date 18-9-18
 */
public class AIOServer {

    //线程池，提高服务效率
    private ExecutorService service;

    //服务端通道
    private AsynchronousServerSocketChannel serverSocketChannel;

    public AIOServer(int port) {
        init(port);
    }

    private void init(int port) {

        try {
            System.out.println("server starting at port : " + port + "...");
            //定义线程池
            service = Executors.newFixedThreadPool(4);
            //开启服务端通道
            serverSocketChannel = AsynchronousServerSocketChannel.open();
            //绑定监听端口，但是未监听请求
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("server stared.");
            //开始监听
            serverSocketChannel.accept(this, new AIOServerHandler());
            try {
                TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String []args) {
        new AIOServer(9999);
    }

    public ExecutorService getService() {
        return service;
    }

    public void setService(ExecutorService service) {
        this.service = service;
    }

    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public void setServerSocketChannel(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }
}
