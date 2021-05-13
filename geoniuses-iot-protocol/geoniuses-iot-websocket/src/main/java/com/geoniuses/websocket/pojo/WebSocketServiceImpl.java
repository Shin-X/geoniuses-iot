package com.geoniuses.websocket.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.mapper.TransferMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author ：zyf
 * @date ：2020/6/5 13:49
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,          // corePoolSize
            80,     // maximumPoolSize
            30L,    // keepAliveTime
            TimeUnit.SECONDS,       // timeUnit
            new LinkedBlockingQueue<>(5000) // workQueue
    );
    @Autowired
    private RedisClusterConfig redisClusterConfig;


    @Autowired
    private StompStoreService stompStoreService;

    @Autowired
    TransferMapper transferMapper;

    /**
     * 连接
     *
     * @param ctx
     * @param frame
     */
    @Override
    public void onConnect(ChannelHandlerContext ctx, StompFrame frame) {
        String acceptVersions = frame.headers().getAsString(StompHeaders.ACCEPT_VERSION);

        StompFrame connectedFrame = new DefaultStompFrame(StompCommand.CONNECTED);
        connectedFrame.headers()
                .set(StompHeaders.VERSION, acceptVersions)
                .set(StompHeaders.SERVER, "Stomp-Server")
                .set(StompHeaders.HEART_BEAT, "5000,5000")
                .set(StompHeaders.SESSION, UUID.randomUUID().toString());
        ctx.channel().writeAndFlush(connectedFrame);
    }

    /**
     * 订阅
     *
     * @param ctx
     * @param inboundFrame
     */
    @Override
    public void onSubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        //目的地路径 011291，021291，031291
        String destination = inboundFrame.headers().getAsString(StompHeaders.DESTINATION);
        //订阅id
        String subscriptionId = inboundFrame.headers().getAsString(StompHeaders.ID);
        String tenantId = inboundFrame.headers().getAsString("tenantId");

        if (destination == null || subscriptionId == null) {
            sendErrorFrame("没有请求头", "Required 'destination' or 'id' header missed", ctx);
            return;
        }

        Set<StompSubscription> subscriptions = stompStoreService.get(destination);
        if (subscriptions == null) {
            subscriptions = new HashSet<StompSubscription>();
            Set<StompSubscription> previousSubscriptions = stompStoreService.put(destination, subscriptions);
            if (previousSubscriptions != null) {
                subscriptions = previousSubscriptions;
            }
        }

        StompSubscription subscription = new StompSubscription(subscriptionId, destination, ctx.channel(), tenantId);
        if (subscriptions.contains(subscription)) {
            sendErrorFrame("重复的订阅",
                    "Received duplicate subscription id=" + subscriptionId, ctx);
            return;
        }

        subscriptions.add(subscription);
        ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                stompStoreService.get(subscription.getDestination()).remove(subscription);
            }
        });

        String receiptId = inboundFrame.headers().getAsString(StompHeaders.RECEIPT);
        if (receiptId != null) {
            StompFrame receiptFrame = new DefaultStompFrame(StompCommand.RECEIPT);
            receiptFrame.headers().set(StompHeaders.RECEIPT_ID, receiptId);
            ctx.writeAndFlush(receiptFrame);
        }
    }

    /**
     * 取消订阅
     *
     * @param ctx
     * @param inboundFrame
     */
    @Override
    public void onUnsubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        String subscriptionId = inboundFrame.headers().getAsString(StompHeaders.SUBSCRIPTION);
        for (Map.Entry<String, Set<StompSubscription>> entry : stompStoreService.getAll().entrySet()) {
            Iterator<StompSubscription> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                StompSubscription subscription = iterator.next();
                if (subscription.getId().equals(subscriptionId) && subscription.getChannel().equals(ctx.channel())) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    /**
     * 前端发送数据
     *
     * @param ctx
     * @param inboundFrame
     */
    @Override
    public void onSend(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        String destination = inboundFrame.headers().getAsString(StompHeaders.DESTINATION);
        if (destination == null) {
            sendErrorFrame("missed header", "required 'destination' header missed", ctx);
            return;
        }

        ObjectMapper om = new ObjectMapper();
        //将参数读取到字节数组中
        ByteBuf content = inboundFrame.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        String paramString = new String(bytes);
        //心跳 前端发送数据证明还在活着
        if (paramString.equals("ping")) {
            return;
        } else {
            Map params = null;
            try {
                params = om.readValue(paramString, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String tenantId = params.get("tenantId").toString();
            String userId = params.get("userId").toString();
            //根据user查询订阅的主题和授权的租户
//            List<String> pubTopic = getTopicByUserId(userId);
//            //destination为订阅的地址，如果此用户发送的主题没有在订阅集合里，说明此用户没有在时空云订阅
//            AtomicBoolean flag = new AtomicBoolean(false);
//            if (CollectionUtils.isEmpty(pubTopic)) {
//                return;
//            }
//            pubTopic.forEach(tenantTopic -> {
//                //租户id
//                String tenant = tenantTopic.split("_")[0];
//                //主题
//                String topic = tenantTopic.split("_")[tenantTopic.split("_").length - 1];
//                if (tenantId.equals(tenant)) {
//                    //包含说明为井盖专题
//                    if (destination.contains(",")) {
//                        String[] split = destination.split(",");
//                        for (String str : split) {
//                            if (str.startsWith(topic)) {
//                                flag.set(true);
//                                return;
//                            }
//                        }
//                    } else {
//                        if (topic.startsWith(destination)) {
//                            flag.set(true);
//                            return;
//                        }
//                    }
//                }
//            });
//            if (!flag.get()) {
//                StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE);
//                String id = UUID.randomUUID().toString();
//                messageFrame.headers()
//                        .set(StompHeaders.MESSAGE_ID, id)
//                        //没有订阅返回 500 ，前端判断提示没有在时空云订阅
//                        .set("errorCode", "500");
//                ctx.channel().writeAndFlush(messageFrame);
//                sendErrorFrame("500", destination, ctx);
//                for (Map.Entry<String, Set<StompSubscription>> entry : stompStoreService.getAll().entrySet()) {
//                    Iterator<StompSubscription> iterator = entry.getValue().iterator();
//                    while (iterator.hasNext()) {
//                        StompSubscription subscription = iterator.next();
//                        if (subscription.getId().equals(destination) && subscription.getChannel().equals(ctx.channel())) {
//                            iterator.remove();
//                            return;
//                        }
//                    }
//                }
//            } else {
//                Set<StompSubscription> subscriptions = stompStoreService.get(destination);
//                for (StompSubscription subscription : subscriptions) {
//                    if (subscription.getChannel().equals(ctx.channel())) {
//                        subscription.setTenantId(tenantId);
//                    }
//                }
//            }
            Set<StompSubscription> subscriptions = stompStoreService.get(destination);
            for (StompSubscription subscription : subscriptions) {
                if (subscription.getChannel().equals(ctx.channel())) {
                    subscription.setTenantId(tenantId);
                }
            }
        }
    }

    /**
     * 断开连接
     *
     * @param ctx
     * @param inboundFrame
     */
    @Override
    public void onDisconnect(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        String receiptId = inboundFrame.headers().getAsString(StompHeaders.RECEIPT);
        if (receiptId == null) {
            ctx.close();
            return;
        }

        StompFrame receiptFrame = new DefaultStompFrame(StompCommand.RECEIPT);
        receiptFrame.headers().set(StompHeaders.RECEIPT_ID, receiptId);
        ctx.writeAndFlush(receiptFrame).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 错误数据
     *
     * @param message
     * @param description
     * @param ctx
     */
    @Override
    public void sendErrorFrame(String message, String description, ChannelHandlerContext ctx) {
        StompFrame errorFrame = new DefaultStompFrame(StompCommand.ERROR);
        errorFrame.headers().set(StompHeaders.MESSAGE, message);
        if (description != null) {
            errorFrame.content().writeCharSequence(description, CharsetUtil.UTF_8);
        }

        ctx.writeAndFlush(errorFrame).addListener(ChannelFutureListener.CLOSE);
    }

    //数据封装
    private static StompFrame transformToMessage(StompFrame sendFrame, StompSubscription subscription) {
        StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE, sendFrame.content().retainedDuplicate());
        String id = UUID.randomUUID().toString();
        messageFrame.headers()
                .set(StompHeaders.MESSAGE_ID, id)
                .set(StompHeaders.SUBSCRIPTION, subscription.getId())
                .set(StompHeaders.CONTENT_LENGTH, Integer.toString(messageFrame.content().readableBytes()));

        CharSequence contentType = sendFrame.headers().get(StompHeaders.CONTENT_TYPE);
        if (contentType != null) {
            messageFrame.headers().set(StompHeaders.CONTENT_TYPE, contentType);
        }

        return messageFrame;
    }

    /**
     * 从redis取出用户订阅主题
     *
     * @param userId
     * @return
     */
    private List<String> getTopicByUserId(String userId) {
//        log.info("用户id==》[{}]",userId);
        byte[] bytes = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().get(("iot_" + userId).getBytes());
        ObjectMapper om = new ObjectMapper();
        List set = null;
        try {
            set = om.readValue(bytes, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        List list = (List) set.get(1);
        return list;
    }

   /* @Override
    public void update(Observable o, Object arg) {
        RestTemplate restTemplate = new RestTemplate();
        threadPool.execute(new PubSubProcress(arg.toString(), stompStoreService.getAll(), redisClusterConfig,transferMapper,restTemplate));
    }*/
}
