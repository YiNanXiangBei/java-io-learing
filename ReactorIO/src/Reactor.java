import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yinan
 * @date 18-9-12
 */
public class Reactor {
    private Map<Integer, EventHandler> registerHandlers = new ConcurrentHashMap<>();

    private Selector demultiplexer;

    public Reactor() throws Exception {
        demultiplexer = Selector.open();
    }

    public void registerEventHandler(int eventType, EventHandler eventHandler) throws Exception {
        registerHandlers.put(eventType, eventHandler);
    }

    public void registerChannel(int eventType, SelectableChannel channel) throws Exception {
        channel.register(demultiplexer, eventType);
    }

    public void run() {
        try {
            while (demultiplexer.select() > 0) {
                Set<SelectionKey> readyHandles = demultiplexer.selectedKeys();
                for (SelectionKey handle : readyHandles) {
                    if (handle.isAcceptable()) {
                        EventHandler handler = registerHandlers.get(SelectionKey.OP_ACCEPT);
                        handler.handleEvent(handle);
                    } else if (handle.isReadable()) {
                        ReadEventHandler handler = (ReadEventHandler) registerHandlers.get(SelectionKey.OP_READ);
                        handler.setDemultiplexer(demultiplexer);
                        handler.handleEvent(handle);
                    } else if (handle.isWritable()) {
                        EventHandler handler = registerHandlers.get(SelectionKey.OP_WRITE);
                        handler.handleEvent(handle);
                    } else if (handle.isConnectable()) {
                        EventHandler handler = registerHandlers.get(SelectionKey.OP_CONNECT);
                        handler.handleEvent(handle);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
