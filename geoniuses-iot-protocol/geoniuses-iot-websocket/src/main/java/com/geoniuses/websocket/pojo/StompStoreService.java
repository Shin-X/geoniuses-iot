package com.geoniuses.websocket.pojo;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ：zyf
 * @date ：2020/6/23 9:59
 */
public interface StompStoreService {

    /**
     * 存储会话
     */
    Set<StompSubscription> put(String destination, Set<StompSubscription> subscriptions);

    /**
     * 获取会话
     */
    Set<StompSubscription> get(String destination);

    /**
     * 获取会话
     */
    ConcurrentMap<String, Set<StompSubscription>> getAll();

    /**
     * destination的会话是否存在
     */
    boolean containsKey(String destination);

    /**
     * 删除会话
     */
    void remove(String destination);
}
