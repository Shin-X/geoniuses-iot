package com.geoniuses.core.pojo.water.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author zyf
 * @Date 2019/10/17 13:57
 */
public class WaterDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        System.out.println("in = " + in);
//        for (int i = 0; i < in; i++) {
//
//        }
        in.skipBytes(in.readableBytes());
//        System.out.println("in ====================>= " + in);
    }

}
