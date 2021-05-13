package com.geoniuses.core.pojo.water.coder;//package com.zydl.netty.protocols.weather.service;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.springframework.stereotype.Component;


/**
 * Created  zyf in 2019/10/8 14:50
 */
@Component
@ChannelHandler.Sharable
public class WaterEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("msg = " + msg);
        ctx.writeAndFlush(msg);
    }
}
