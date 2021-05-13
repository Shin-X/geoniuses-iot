package com.geoniuses.mqtt.service.impl;



import com.geoniuses.core.pojo.SessionStore;
import com.geoniuses.core.pojo.SubscribeStore;
import com.geoniuses.mqtt.service.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MqttHandlerServiceImpl implements MqttHandlerService {


    @Autowired
    private SubscribeStoreService subscribeStoreService;
    @Autowired
    private SessionStoreService sessionStoreService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ParseMessageService parseMessageService;

    /**
     * 连接
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void connectMessage(ChannelHandlerContext ctx, MqttConnectMessage msg) {
        log.info("处理Connect的数据");
        // 消息解码器出现异常
        if (msg.decoderResult().isFailure()) {
            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
                ctx.channel().writeAndFlush(connAckMessage);
                ctx.channel().close();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的clientId
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
                ctx.channel().writeAndFlush(connAckMessage);
                ctx.channel().close();
                return;
            }
            ctx.channel().close();
            return;
        }

        // clientId为空或null的情况, 这里要求客户端必须提供clientId
        if (StringUtils.isEmpty(msg.payload().clientIdentifier())) {
            log.error("clientId为空");
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
            ctx.channel().writeAndFlush(connAckMessage);
            ctx.channel().close();
            return;
        }

        //用户名密码验证
//        String username = msg.payload().userName();
//        String password = msg.payload().passwordInBytes() == null ? null : new String(msg.payload().passwordInBytes(), CharsetUtil.UTF_8);
//        if (!authService.checkValid(username, password)) {
//            log.error("用户名或密码错误，用户名={},密码={}", username, password);
//            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
//                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
//                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
//            ctx.channel().writeAndFlush(connAckMessage);
//            ctx.channel().close();
//            return;
//        }


        //处理遗嘱信息,当启用遗嘱消息功能时，服务端先存储遗嘱消息,当服务端认为连接异常时，即可发布遗嘱消息
        SessionStore sessionStore = new SessionStore(msg.payload().clientIdentifier(), ctx.channel(), msg.variableHeader().isCleanSession(), null);
        if (msg.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(msg.variableHeader().willQos()), msg.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(msg.payload().willTopic(), 0),
                    Unpooled.buffer().writeBytes(msg.payload().willMessageInBytes()));
            sessionStore.setWillMessage(willMessage);
        }


        //存储会话消息及返回接受客户端连接
        sessionStoreService.put(msg.payload().clientIdentifier(), sessionStore);

        //将clientId存储到channel的map中
        ctx.channel().attr(AttributeKey.valueOf("clientId")).set(msg.payload().clientIdentifier());
        Boolean sessionPresent = sessionStoreService.containsKey(msg.payload().clientIdentifier()) && !msg.variableHeader().isCleanSession();
        MqttConnAckMessage connAck = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent),
                null
        );
        ctx.channel().writeAndFlush(connAck);
        System.out.println("连接消息 msg = " + msg);

    }

    /**
     * 发布
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void publishMessage(ChannelHandlerContext ctx, MqttPublishMessage msg) {
        ByteBuf content = msg.content();
        log.info("收到发布消息msg [{}]",ByteBufUtil.hexDump(content));
        // QoS=0
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), msg, false, false);
        }
        // QoS=1
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            log.debug("收到的topic 【{}】",msg.variableHeader().topicName());
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), msg, false, false);
            this.sendPubBack(ctx, msg.variableHeader().packetId());
//            msg.content().retain();
            parsePublishMessage(msg);
        }
        // QoS=2
        if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), msg, false, false);
            this.sendPubRec(ctx, msg.variableHeader().packetId());
        }
    }



    private void parsePublishMessage(MqttPublishMessage msg) {
        parseMessageService.doMessage(msg);
    }

    /**
     * 发送qos1 publish  确认消息
     */
    private void sendPubBack(ChannelHandlerContext ctx, int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                MqttMessageType.PUBACK,
                false,
                MqttQoS.AT_MOST_ONCE,
                false,
                0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader, from);
        ctx.channel().writeAndFlush(mqttPubAckMessage);
    }

    /**
     * 发送qos2 确认消息
     *
     * @param ctx
     * @param messageId
     */
    private void sendPubRec(ChannelHandlerContext ctx, int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader, from);
        Channel channel = ctx.channel();
        channel.writeAndFlush(mqttPubAckMessage);
    }

    /**
     * 发布消息
     *
     * @param topic
     * @param mqttQoS
     * @param msg
     * @param retain
     * @param dup
     */
    private void sendPublishMessage(String topic, MqttQoS mqttQoS, MqttPublishMessage msg, boolean retain, boolean dup) {
        List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
        byte[] messageBytes = new byte[msg.payload().readableBytes()];
        msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
        if (CollectionUtils.isEmpty(subscribeStores))
            return;
        subscribeStores.forEach(subscribeStore -> {

            if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
                // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
                MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS().value() ? MqttQoS.valueOf(subscribeStore.getMqttQoS().value()) : mqttQoS;
                if (respQoS == MqttQoS.AT_MOST_ONCE) {
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, 0),
                            Unpooled.buffer().writeBytes(messageBytes));
                    log.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
                if (respQoS == MqttQoS.AT_LEAST_ONCE) {
                    int messageId = msg.variableHeader().packetId();
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
                if (respQoS == MqttQoS.EXACTLY_ONCE) {
                    int messageId = msg.variableHeader().packetId();
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
            }


        });
    }

    /**
     * 订阅
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void subMessage(ChannelHandlerContext ctx, MqttSubscribeMessage msg) {
//        msg.payload().topicSubscriptions():
        String clientId = (String) ctx.channel().attr(AttributeKey.valueOf("clientId")).get();
        List<MqttTopicSubscription> mqttTopicSubscriptions = msg.payload().topicSubscriptions();
        List<Integer> mqttQosList = new ArrayList<>();
        mqttTopicSubscriptions.forEach(subscriptions -> {
            SubscribeStore subscribeStore = new SubscribeStore(clientId, subscriptions.topicName(), subscriptions.qualityOfService());

            mqttQosList.add(subscribeStore.getMqttQoS().value());

            subscribeStoreService.put(subscribeStore.getTopicFilter(), subscribeStore);
        });


        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK,
                false,
                MqttQoS.AT_MOST_ONCE,
                false,
                0);

        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(msg.variableHeader().messageId());

        MqttSubAckPayload payload = new MqttSubAckPayload(mqttQosList);

        MqttSubAckMessage mqttSubAckMessage = new MqttSubAckMessage(fixedHeader, variableHeader, payload);

        ctx.channel().writeAndFlush(mqttSubAckMessage);

    }

    @Override
    public void pingreqMessage(ChannelHandlerContext ctx, MqttMessage msg) {
        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                null,
                null);
//        log.info("PINGREQ - clientId: {}", ctx.channel().attr(AttributeKey.valueOf("clientId")).get());
        ctx.channel().writeAndFlush(pingRespMessage);
    }

    @Override
    public void pingrespMessage(ChannelHandlerContext ctx, MqttMessage msg) {

    }

    /**
     * 取消订阅
     *
     * @param ctx
     * @param mqttMessage
     */
    @Override
    public void unSubMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttUnsubscribeMessage msg = (MqttUnsubscribeMessage) mqttMessage;
        List<String> topicFilters = msg.payload().topics();
        String clinetId = (String) ctx.channel().attr(AttributeKey.valueOf("clientId")).get();
        topicFilters.forEach(topicFilter -> {
            subscribeStoreService.remove(topicFilter, clinetId);
            log.info("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clinetId, topicFilter);
        });
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                null);
        ctx.channel().writeAndFlush(unsubAckMessage);
    }
}
