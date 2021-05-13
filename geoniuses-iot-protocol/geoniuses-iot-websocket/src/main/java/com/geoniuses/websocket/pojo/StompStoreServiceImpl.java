package com.geoniuses.websocket.pojo;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ：zyf
 * @date ：2020/6/23 10:00
 */
@Service
public class StompStoreServiceImpl implements StompStoreService {
    //key：目的地
    private final ConcurrentMap<String, Set<StompSubscription>> destinations = new ConcurrentHashMap<String, Set<StompSubscription>>();

    @Override
    public Set<StompSubscription> put(String destination, Set<StompSubscription> subscriptions) {
        return destinations.putIfAbsent(destination, subscriptions);
    }

    @Override
    public Set<StompSubscription> get(String destination) {
        return destinations.get(destination);
    }

    @Override
    public ConcurrentMap<String, Set<StompSubscription>> getAll() {
        return destinations;
    }

    @Override
    public boolean containsKey(String destination) {
        return false;
    }

    @Override
    public void remove(String destination) {
        destinations.remove(destination);
    }
}
