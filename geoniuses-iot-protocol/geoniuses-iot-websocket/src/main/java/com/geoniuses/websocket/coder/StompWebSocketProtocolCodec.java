package com.geoniuses.websocket.coder;


import com.geoniuses.websocket.StompHandler;
import com.geoniuses.websocket.pojo.WebSocketService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.handler.codec.stomp.StompSubframeAggregator;
import io.netty.handler.codec.stomp.StompSubframeDecoder;
import io.netty.handler.codec.stomp.StompSubframeEncoder;

import java.util.List;

/**
 * @author ：zyf
 * @date ：2020/6/3 11:05
 */
public class StompWebSocketProtocolCodec extends MessageToMessageCodec<WebSocketFrame, ByteBuf> {

    private WebSocketService webSocketService;

    public StompWebSocketProtocolCodec(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HandshakeComplete) {
            ctx.pipeline()
                    .addLast(new StompSubframeDecoder())
                    .addLast(new StompSubframeEncoder())
                    .addLast(new StompSubframeAggregator(65536))
                    .addLast(new StompHandler(webSocketService));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame, List<Object> out) {
        if (webSocketFrame instanceof TextWebSocketFrame) {
            out.add(webSocketFrame.content().retain());
        } else {
            ctx.close();
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf stompFrame, List<Object> out) throws Exception {
        out.add(new TextWebSocketFrame(stompFrame.retain()));
    }
}
