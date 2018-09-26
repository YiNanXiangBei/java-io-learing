package method;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yinan
 * @date created in 上午11:03 18-9-26
 */
public class ListenCall {

    ConcurrentHashMap<String, GofFunction<ParamContext, ParamContext>> methodMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ParamContext> paramMap = new ConcurrentHashMap<>();

    /**
     * 获取请求编号
     * @return
     */
    public String getCallId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 监听返回值
     * @param method
     * @param callId
     * @param context
     */
    public void listenResult(GofFunction<ParamContext, ParamContext> method, String callId, ParamContext context) {
        methodMap.put(callId, method);
        paramMap.put(callId, context);
    }

    public void waitForResult(ParamContext result, String callId) {
        GofFunction<ParamContext, ParamContext> function = methodMap.get(callId);
        if (function != null) {
            ParamContext context = paramMap.get(callId);
            if (context == null) {
                context = new ParamContext();
            }
            function.execute(result, context);
        }
    }

}
