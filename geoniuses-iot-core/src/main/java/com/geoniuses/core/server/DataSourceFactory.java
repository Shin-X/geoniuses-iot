package com.geoniuses.core.server;

import com.geoniuses.core.config.ElasticsearchConfig;
import com.geoniuses.core.config.KafkaConfiguration;
import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.config.ThreadPoolConfig;
import com.geoniuses.core.mapper.TransferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Author liuxin
 * @Date: 2021/5/12 13:43
 * @Description:
 */
@Service
public class DataSourceFactory implements AbstractDataSourceFactory {

    private volatile static DataSourceFactory instance;

    private DataSourceFactory() {
    }
    //单例模式
    public static DataSourceFactory getInstance() {
        if (instance == null) {
            //只有在第一次创建对象的时候需要加锁，之后就不需要了
            synchronized (instance) {
                if (instance == null) {
                    instance = new DataSourceFactory();
                }
            }
        }
        return instance;
    }
    @Autowired
    KafkaConfiguration kafkaConfiguration;
    @Autowired
    RedisClusterConfig redisClusterConfig;
    @Autowired
    ThreadPoolConfig threadPoolConfig;
    @Autowired
    ElasticsearchConfig elasticsearchConfig;
    @Autowired
    TransferMapper transferMapper;


    @Override
    public Object createDataSource(String dataType) {
        switch (dataType){
            case "kafka":
                return kafkaConfiguration;
            case "redis":
                return redisClusterConfig;
            case "thread":
                return threadPoolConfig;
            case "es":
                return elasticsearchConfig;
            case "sql":
                return transferMapper;
        }
            return null;
    }
}
