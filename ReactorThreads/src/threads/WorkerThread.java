package threads;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.*;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yinan
 * @date created in 上午10:23 18-9-18
 * 针对增加和删除添加了锁
 */
public class WorkerThread extends EventHandler implements Runnable {

    private Selector selector;

    private List<SocketChannel> registList = new ArrayList<>();

    private ReentrantLock lock = new ReentrantLock();

    public WorkerThread() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(SocketChannel socketChannel) {
        if (socketChannel != null) {
            try {
                lock.lock();
                registList.add(socketChannel);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            Set<SelectionKey> ops = null;
            try {
                selector.select(1500);
                ops = selector.selectedKeys();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            super.handler(ops, selector);
            registEvent();
        }
    }

    //注册channel到selector，监听read
    private void registEvent() {
        //注册事件
        if (! registList.isEmpty()) {
            try {
                lock.lock();
                for (Iterator<SocketChannel> it = registList.iterator(); it.hasNext();) {
                    SocketChannel socketChannel = it.next();
                    try {
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    }
                    it.remove();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
