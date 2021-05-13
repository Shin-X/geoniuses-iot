package com.geoniuses.websocket.pojo;


import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.mapper.TransferMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * @auther: zyf
 * @Date: 2020/09/27 16:59
 * @Description
 */
@Component
public class KafkaConsumerPubSub {

    @Autowired
    private RedisClusterConfig redisClusterConfig;

    @Autowired
    private TransferMapper mapper;

    @Autowired
    private StompStoreService stompStoreService;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier(value = "pubSubPoolExecutor")
    private ThreadPoolTaskExecutor pubsubExecutor;

    @KafkaListener(id = "pubsubConsumer", topicPattern = "ZYDL_WellCover99_Push_Airport.*", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String,String> record, Acknowledgment ack){
        if (null == record){
            return;
        }
        pubsubExecutor.execute(new PubSubProcress(record.value(), stompStoreService.getAll(), redisClusterConfig, mapper,restTemplate));
        ack.acknowledge();
    }
}
