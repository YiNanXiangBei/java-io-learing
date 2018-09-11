import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author yinan
 * @date created in 上午10:20 18-9-11
 */
public class Client {
    public static void main(String[] args) {

        try (Socket client = new Socket("127.0.0.1", 8888)){
            client.setSoTimeout(10000);
            //获取键盘输入
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            //设置输出流，将客户端数据发送到服务端
            PrintStream out = new PrintStream(client.getOutputStream());
            //获取socket输入流，接收服务端发送过来的数据
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean flag = true;
            while (flag) {
                System.out.print("输入信息：");
                //readLine()阻塞方法，直到有返回值
                String str = input.readLine();
                out.println(str);
                if ("bye".equals(str)) {
                    flag = false;
                } else {
                    try {
                        String echo = buf.readLine();
                        System.out.println(echo);
                    } catch (SocketTimeoutException e) {
                        System.out.println("Time out, No process");
                    }
                }
            }
            input.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
