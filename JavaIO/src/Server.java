import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date created in 上午10:20 18-9-11
 */
public class Server {
    public static void main(String[] args) throws IOException {
        //服务端监听端口，获取tcp连接
        ServerSocket server = new ServerSocket(8888);
        Socket client = null;
        boolean flag = true;
        while (flag) {
            client = server.accept();
            System.out.println("与客户端连接成功");

            new ThreadPoolExecutor(4, 8, 8, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100)).execute(new ServerThread(client));

        }



        server.close();
    }
}

class ServerThread implements Runnable {

    private Socket client = null;
    ServerThread (Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (PrintStream out = new PrintStream(client.getOutputStream());
             BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()))){
            boolean flag = true;
            while (flag) {
                //从客户端接收发送过来的数据
                String str = buf.readLine();
                System.out.println(Thread.currentThread().getName()+ " : " + str);
                if (str == null || "".equals(str)) {
                    flag = false;
                } else {
                    if ("bye".equals(str)) {
                        flag = false;
                    } else {
                        //将接收到的字符串返回
//                        TimeUnit.SECONDS.sleep(8);
                        out.println("echo: " + str);
                    }
                }
            }

            out.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}