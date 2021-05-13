package com.geoniuses.core.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * kafka配置类
 */
@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Value("${spring.kafka.username}")
    private String username;

    @Value("${spring.kafka.password}")
    private String password;


    //生产者工厂
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory(producerProps());
    }

    /**
     * 单条监听
     * @return
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //当使用手动提交时必须设置ackMode为MANUAL,否则会报错No Acknowledgment available as an argument, the listener container must have a MANUAL AckMode to populate the Acknowledgment.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.getContainerProperties().setAckCount(10);
//        factory.setBatchListener(true);
        factory.getContainerProperties().setAckTime(10000);
        factory.getContainerProperties().setGroupId("consumer_smart_pubsub");
        factory.setConcurrency(10);
        factory.getContainerProperties().setPollTimeout(5000);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactoryEs() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //当使用手动提交时必须设置ackMode为MANUAL,否则会报错No Acknowledgment available as an argument, the listener container must have a MANUAL AckMode to populate the Acknowledgment.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setAckCount(10);
        //批量监听
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckTime(10000);
        factory.getContainerProperties().setGroupId("consumer_smart_es");
        factory.setConcurrency(10);
        factory.getContainerProperties().setPollTimeout(5000);
        return factory;
    }

    //生产者配置参数
    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap();
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaServer);
        //重试，0为不启用重启机制
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        //控制批处理大小，字节为单位 默认16kb 即16384 bytes
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 204800);
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG,1048576);
        //批量发送，延迟为1毫秒，启用该功能能有效减少生产者发送消息次数，从而提高并发量
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);

        props.put(ProducerConfig.ACKS_CONFIG, "1");

        //生产者可以使用的总内存字节来缓冲等待发送到服务器的记录 默认32MB
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 16777216);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 209715200);
        //键的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //值的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(ProducerConfig.CLIENT_ID_CONFIG, "nettyKafkaProducer" + UUID.randomUUID());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username + "\" password=\"" + password + "\";");


        return props;
    }

    //消费者工厂
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory(consumerProps());
    }

    //消费者配置参数
    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap();
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaServer);
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka3:9092,kafka4:9092,kafka5:9092,kafka6:9092,kafka7:9092");
        //GroupId
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"updateMysqlGroup");
        //是否自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
        //自动提交频率
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"100");
        //Session超时设置
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");

        //heartbeat.interval.ms一般是session.timeout.ms的三分之一，
        //并且session.timeout.ms在group.min.session.timeout.ms（默认6秒）和group.max.session.timeout.ms（默认30秒）范围之间。
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");

        //从最后一条开始获取数据
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        //键的序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //值得序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //每一批数量
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
//        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 1024);
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1000);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username + "\" password=\"" + password + "\";");

        return props;
    }

    @Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate template = new KafkaTemplate<String, String>(producerFactory());
        return template;
    }

    @Bean
    @Primary
    public LinkedBlockingQueue queue() {
        LinkedBlockingQueue<Producer> deque = new LinkedBlockingQueue<Producer>(10);
        for (int i = 0; i < 10; i++) {
            KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerProps());
            deque.add(kafkaProducer);
        }

        return deque;
    }

}
