package com.geoniuses.websocket;





import com.geoniuses.websocket.pojo.WebSocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.stomp.StompFrame;


/**
 * @author ：zyf
 * @date ：2020/6/2 11:46
 */
public class StompHandler extends SimpleChannelInboundHandler<StompFrame> {

    private WebSocketService webSocketService;

    public StompHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
//        System.out.println("错误信息===========》 " + cause.getMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StompFrame frame) {
        DecoderResult decoderResult = frame.decoderResult();
        if (decoderResult.isFailure()) {
            webSocketService.sendErrorFrame("rejected frame", decoderResult.toString(), ctx);
            return;
        }
        System.out.println("收到前端的数据================》 " + frame);
        switch (frame.command()) {
            case STOMP:
            case CONNECT:
                webSocketService.onConnect(ctx, frame);
                break;
            case SUBSCRIBE:
                webSocketService.onSubscribe(ctx, frame);
                break;
            case UNSUBSCRIBE:
                webSocketService.onUnsubscribe(ctx, frame);
                break;
            case SEND:
                webSocketService.onSend(ctx, frame);
                break;
            case DISCONNECT:
                webSocketService.onDisconnect(ctx, frame);
                break;
            default:
                webSocketService.sendErrorFrame("不支持的命令", "收到的命令 " + frame.command(), ctx);
                break;
        }
    }
}

