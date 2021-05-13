package com.geoniuses.tcp.coder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @Author zyf
 * @Date 2019/10/25 9:49
 */
public class ManholeCoverEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // TODO Auto-generated method stub
        if (msg != null) {
            ctx.writeAndFlush(msg);
        }
    }
}
