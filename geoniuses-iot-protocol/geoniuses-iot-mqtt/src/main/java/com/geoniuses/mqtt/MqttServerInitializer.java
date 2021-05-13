package com.geoniuses.mqtt;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttServerInitializer extends ChannelInitializer<NioSocketChannel> {

    @Autowired
    private MqttServerHandler mqttServerHandler;

    @Override
    protected void initChannel(NioSocketChannel ch) {
        ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
        ch.pipeline().addLast(new MqttDecoder());
        ch.pipeline().addLast(mqttServerHandler);
        ch.pipeline().addLast(MqttEncoder.INSTANCE);
    }
}
