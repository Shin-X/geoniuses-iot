package com.geoniuses.tcp.service;



import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.config.ThreadPoolConfig;
import com.geoniuses.core.mapper.TransferMapper;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ：zyf
 * @date ：2020/7/6 12:15
 */
public interface PackageStoreService {

    void store(ThreadPoolConfig threadPoolConfig, Map<String, Object> dataByForm, TransferMapper transferMapper, LinkedBlockingQueue linkedBlockingQueue, RedisClusterConfig redisClusterConfig);
}
