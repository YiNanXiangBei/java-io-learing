package update;


import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author yinan
 * @date 18-9-17
 */
public class MainReactor implements Runnable {

    private Selector deMultiplexer;

    public MainReactor(Selector deMultiplexer) {
        this.deMultiplexer = deMultiplexer;
    }

    @Override
    public void run() {
        System.out.println("线程： " + Thread.currentThread().getName() + " 开始！");
        while (!Thread.interrupted()) {
            try {
                deMultiplexer.select();

                Set<SelectionKey> readyHandles =
                        deMultiplexer.selectedKeys();
                Iterator<SelectionKey> handleIterator =
                        readyHandles.iterator();

                while (handleIterator.hasNext()) {
                    SelectionKey handle = handleIterator.next();
                    if (handle.isAcceptable()) {
                        IEventHandler handler =
                                new AcceptEventHandler(deMultiplexer);
                        handler.handleEvent(handle);
                        handleIterator.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
