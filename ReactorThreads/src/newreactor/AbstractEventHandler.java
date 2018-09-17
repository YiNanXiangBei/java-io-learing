package newreactor;

import java.nio.channels.SelectionKey;

/**
 * @author yinan
 * @date created in 下午2:39 18-9-17
 */
public abstract class AbstractEventHandler implements Runnable {

    public void handleEvent(SelectionKey handle) throws Exception {}

}
