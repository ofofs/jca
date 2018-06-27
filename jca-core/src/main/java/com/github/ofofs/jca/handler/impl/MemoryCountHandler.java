package com.github.ofofs.jca.handler.impl;

import com.github.ofofs.jca.handler.CountHandler;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author kangyonggan
 * @since 6/27/18
 */
public class MemoryCountHandler implements CountHandler {

    private static Map<String, LinkedBlockingDeque<Long>> map = new HashMap();
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public synchronized boolean isViolate(String key, long during, int count) {
        // 获取一个指定key的队列。（如果不存在则创建，长度等于count）
        LinkedBlockingDeque<Long> queue = getQueue(key, count);

        // 当队列满了的时候才有可能超出最大调用次数，才需要我们处理
        if (queue.size() >= count) {
            try {
                Long first = queue.takeFirst();
                queue.addLast(System.currentTimeMillis());

                // 如果队尾时间（当前时间） - 队首时间 < 时间段 则表明此时间段内调用次数大于指定次数了
                if (during > queue.getLast() - first) {
                    return true;
                }
            } catch (InterruptedException e) {
                // 对于异常有很多处理方式，本实现处理成超过最大调用次数
                return true;
            }
        } else {
            // 队列没满，不会超过最大调用次数，只需更新最后调用时间
            queue.addLast(System.currentTimeMillis());
        }

        return false;
    }

    /**
     * 获取一个指定key的队列。（如果不存在则创建，长度等于count）
     *
     * @param key   队列的键
     * @param count 队列的大小
     * @return 返回队列
     */
    private LinkedBlockingDeque<Long> getQueue(String key, int count) {
        LinkedBlockingDeque<Long> queue = map.get(key);
        if (queue == null) {
            queue = new LinkedBlockingDeque(count);
            map.put(key, queue);
        }

        return queue;
    }
}
