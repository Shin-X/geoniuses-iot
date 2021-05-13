package com.geoniuses.mqtt;



import com.geoniuses.mqtt.service.MqttHandlerService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class MqttServerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Autowired
    private MqttHandlerService mqttHandlerService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) {
        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                mqttHandlerService.connectMessage(ctx, (MqttConnectMessage) msg);
                break;
            case PUBLISH:
                mqttHandlerService.publishMessage(ctx, (MqttPublishMessage) msg);
                break;
            case SUBSCRIBE:
                mqttHandlerService.subMessage(ctx, (MqttSubscribeMessage) msg);
                break;
            case UNSUBSCRIBE:
                mqttHandlerService.unSubMessage(ctx, msg);
                break;
            case PINGREQ:
                mqttHandlerService.pingreqMessage(ctx, msg);
                break;
            case PUBACK:
//                mqttHandlerService.doPubAck(ctx, msg);
                break;
            case PUBREC:
                break;
            case PUBREL:
                break;
            case PUBCOMP:
                break;
            case UNSUBACK:
                break;
            case PINGRESP:
                break;
            case DISCONNECT:
                ctx.close();
                break;
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
