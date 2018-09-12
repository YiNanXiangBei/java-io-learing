import java.nio.channels.SelectionKey;

/**
 * @author yinan
 * @date 18-9-12
 */
public interface EventHandler {

    void handleEvent(SelectionKey handle) throws Exception;
}
