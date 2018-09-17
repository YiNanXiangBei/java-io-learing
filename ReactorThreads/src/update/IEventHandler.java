package update;

import java.nio.channels.SelectionKey;

/**
 * @author yinan
 * @date 18-9-17
 */
public interface IEventHandler {

    void handleEvent(SelectionKey handle) throws Exception;

}
