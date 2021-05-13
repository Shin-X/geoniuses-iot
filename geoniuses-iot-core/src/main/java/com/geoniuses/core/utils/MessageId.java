package com.geoniuses.core.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成messgaeId
 **/
public class MessageId {

    private static AtomicLong index = new AtomicLong(1);

    /**
     * 获取messageId
     *
     * @return id
     */
    public static long messageId() {
        for (;;) {
            long current = index.get();
            long next = (current >= Long.MAX_VALUE ? 0 : current + 1);
            if (index.compareAndSet(current, next)) {
                return current;
            }
        }
    }
}
