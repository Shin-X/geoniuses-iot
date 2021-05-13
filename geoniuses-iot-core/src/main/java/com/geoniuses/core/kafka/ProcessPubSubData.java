package com.geoniuses.core.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ：zyf
 * @date ：2020/4/27 17:42
 */

public class ProcessPubSubData implements Runnable {

    private Map<String, Object> map;

    private LinkedBlockingQueue deque;

    private static final Logger logger = LogManager.getLogger(ProcessPubSubData.class);

    public ProcessPubSubData(Map<String, Object> map, LinkedBlockingQueue deque) {
        this.map = map;
        this.deque = deque;
    }

    @Override
    public void run() {
//        long starttime = System.currentTimeMillis();
        String pubSubTopic = map.get("pubSubTopic").toString();
        String jsonStr = new JSONObject(map).toJSONString();
        ProducerRecord record = new ProducerRecord(pubSubTopic, jsonStr);
        Producer producer = null;
        try {
            producer = (Producer) deque.take();
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "推送数据线程池错误！！", e);
            Thread.currentThread().interrupt();
        }
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
//                System.out.println("推送offset:" + metadata.offset() +
//                        "\n推送partition:" + metadata.partition() +
//                        "\n推送topic:"+ metadata.topic() +"\n");
                if (exception == null) {
//                    System.out.println("\n发送到推送数据应答-ack : " + (System.currentTimeMillis() - starttime) + "ms");
                }
            }
        });
        try {
            deque.put(producer);
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "推送数据线程池错误！！", e);
            Thread.currentThread().interrupt();
        }
    }
}
