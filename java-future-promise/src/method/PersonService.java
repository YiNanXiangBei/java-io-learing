package method;

import java.util.concurrent.TimeUnit;

/**
 * @author yinan
 * @date created in 上午11:09 18-9-26
 */
public class PersonService {

    private ListenCall listenCall = new ListenCall();

    public void getPwdFromDb(String name, String callId) {
        new Thread(() -> {
            String sql = "select * from person where name = '" + name + "'";
            String pwd = "111";
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waitForResult(new ParamContext("pwd", pwd), callId);
        });
    }

    public String getCallId() {
        return listenCall.getCallId();
    }

    public void waitForResult(ParamContext p, String callId) {
        listenCall.waitForResult(p, callId);
    }

    public void listenResult(GofFunction<ParamContext, ParamContext> method, String callId, ParamContext context) {
        listenCall.listenResult(method, callId, context);
    }

}
