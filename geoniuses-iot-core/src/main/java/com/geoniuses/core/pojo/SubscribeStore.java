package com.geoniuses.core.pojo;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

import java.io.Serializable;

/**
 * 订阅存储实体类
 */
@Data
public class SubscribeStore implements Serializable {
    private static final long serialVersionUID = 1276156087085594264L;

    private String clientId;    //客户端唯一标识

    private String topicFilter; //主题名称

    private MqttQoS mqttQoS;    //服务质量 0 ，1 ，2

    public SubscribeStore(String clientId, String topicFilter, MqttQoS mqttQoS) {
        this.clientId = clientId;
        this.topicFilter = topicFilter;
        this.mqttQoS = mqttQoS;
    }
//
//    public String getClientId() {
//        return clientId;
//    }
//
//    public SubscribeStore setClientId(String clientId) {
//        this.clientId = clientId;
//        return this;
//    }
//
//    public String getTopicFilter() {
//        return topicFilter;
//    }
//
//    public SubscribeStore setTopicFilter(String topicFilter) {
//        this.topicFilter = topicFilter;
//        return this;
//    }
//
//    public MqttQoS getMqttQoS() {
//        return mqttQoS;
//    }
//
//    public SubscribeStore setMqttQoS(MqttQoS mqttQoS) {
//        this.mqttQoS = mqttQoS;
//        return this;
//    }
}
