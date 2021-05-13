package com.geoniuses.mqtt.service;

import io.netty.handler.codec.mqtt.MqttPublishMessage;

public interface ParseMessageService {
    void doMessage(MqttPublishMessage msg);
}
