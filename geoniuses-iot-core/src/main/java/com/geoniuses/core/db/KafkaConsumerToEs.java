package com.geoniuses.core.db;



import com.geoniuses.core.config.ElasticsearchConfig;
import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.mapper.TransferMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：zyf
 * @date ：2020/9/4 14:39
 */
@Component
public class KafkaConsumerToEs {

    private static final Logger logger = LogManager.getLogger(KafkaConsumerToEs.class);

    @Autowired
    private ThreadPoolTaskExecutor execute;
    @Autowired
    TransferMapper transferMapper;
    @Autowired
    private RedisClusterConfig redisClusterConfig;
    @Autowired
    private ElasticsearchConfig elasticsearchConfig;

    @KafkaListener(id = "esConsumer", containerFactory = "kafkaListenerContainerFactoryEs", topicPattern = "ZYDL_WellCover99_Airport.*")
    public void listen(List<ConsumerRecord<String,String>> records, Acknowledgment ack) {
        if (records.size() > 0) {
            System.out.println("records = " + records);
            execute.execute(new CurrentToEs(records, elasticsearchConfig, redisClusterConfig));
            ack.acknowledge();
        }
    }
}
