package com.geoniuses.websocket.pojo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.StompFrame;

import java.util.Observable;

/**
 * @author ：zyf
 * @date ：2020/6/5 13:49
 */
public interface WebSocketService {
    void onConnect(ChannelHandlerContext ctx, StompFrame frame);

    void onSubscribe(ChannelHandlerContext ctx, StompFrame frame);

    void onUnsubscribe(ChannelHandlerContext ctx, StompFrame frame);

    void onSend(ChannelHandlerContext ctx, StompFrame frame);

    void onDisconnect(ChannelHandlerContext ctx, StompFrame frame);

    void sendErrorFrame(String message, String description, ChannelHandlerContext ctx);

}
