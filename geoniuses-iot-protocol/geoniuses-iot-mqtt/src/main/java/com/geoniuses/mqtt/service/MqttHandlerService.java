package com.geoniuses.mqtt.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;

public interface MqttHandlerService {

    void connectMessage(ChannelHandlerContext ctx, MqttConnectMessage msg);

    void publishMessage(ChannelHandlerContext ctx, MqttPublishMessage msg);

    void subMessage(ChannelHandlerContext ctx, MqttSubscribeMessage msg);

    void pingreqMessage(ChannelHandlerContext ctx, MqttMessage msg);

    void pingrespMessage(ChannelHandlerContext ctx, MqttMessage msg);

    void unSubMessage(ChannelHandlerContext ctx, MqttMessage msg);
}
