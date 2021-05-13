package com.geoniuses.udp.coder;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class DATA_6216Encoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        // TODO Auto-generated method stub
        if (msg != null) {
            ctx.writeAndFlush(msg);
        }
    }

}
