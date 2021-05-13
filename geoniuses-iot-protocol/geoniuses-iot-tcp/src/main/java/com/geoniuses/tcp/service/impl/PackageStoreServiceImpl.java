package com.geoniuses.tcp.service.impl;


import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.config.ThreadPoolConfig;
import com.geoniuses.core.kafka.ProcessPubSubData;
import com.geoniuses.core.mapper.TransferMapper;
import com.geoniuses.core.pojo.ParseMysqlToSyncTopic;
import com.geoniuses.core.pojo.ProcessData;
import com.geoniuses.tcp.TcpServer;
import com.geoniuses.tcp.service.PackageStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ：zyf
 * @date ：2020/7/6 12:19
 */
@Service
public class PackageStoreServiceImpl implements PackageStoreService {

    @Override
    public void store(ThreadPoolConfig threadPoolConfig, Map<String, Object> dataByForm, TransferMapper transferMapper, LinkedBlockingQueue linkedBlockingQueue, RedisClusterConfig redisClusterConfig) {
        //发送kafka线程池
        threadPoolConfig.threadPoolExecutor().execute(new ProcessData(dataByForm, transferMapper, linkedBlockingQueue, redisClusterConfig));
        //更新mysql线程池
        threadPoolConfig.threadPoolExecutor().execute(new ParseMysqlToSyncTopic(dataByForm, transferMapper, linkedBlockingQueue, redisClusterConfig));
//        //发布订阅线程池
        dataByForm.put("pubSubTopic", TcpServer.PUBSUB_TOPIC);
        threadPoolConfig.pubSubPoolExecutor().execute(new ProcessPubSubData(dataByForm,linkedBlockingQueue));
    }
}
