package method;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yinan
 * @date created in 上午10:58 18-9-26
 */
public class ParamContext {

    private Map<String, Object> datas = new HashMap<>();
    public ParamContext(Object ...params) {
        if (params == null || params.length == 0) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
            datas.put((String) params[i], params[i + 1]);
            i += 2;
        }
    }

    @SuppressWarnings("unchecked")
    public <R> R get(String key) {
        return (R)datas.get(key);
    }

}
