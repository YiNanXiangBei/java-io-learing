package method;

import java.util.concurrent.CountDownLatch;

/**
 * @author yinan
 * @date created in 上午11:15 18-9-26
 */
public class PersonManager {

    PersonService personService = new PersonService();

    public static void main(String[] args) throws InterruptedException {
        PersonManager personManager = new PersonManager();
        personManager.seePwd();
        new CountDownLatch(1).await();
    }

    private void seePwd() {
        String name = "1111";
        String time = String.valueOf(System.currentTimeMillis());
        String callId = personService.getCallId();
        personService.getPwdFromDb(name, callId);
        personService.listenResult(this::getPwd, callId, new ParamContext("time", time));
        System.out.println("数据库读取数据， 可能有点耗时， 这里可以做成异步操作，我先做其他事情。。。。。。");
    }

    private void getPwd(ParamContext result, ParamContext context) {
        String pwd = result.get("pwd");
        long sed = (System.currentTimeMillis() - Long.valueOf(context.get("time"))) / 1000;
        System.out.println("经过 " + sed + " 秒" + "查询用户名密码的调用终于返回了" );
        System.out.println("得到密码： " + pwd);
    }

}
