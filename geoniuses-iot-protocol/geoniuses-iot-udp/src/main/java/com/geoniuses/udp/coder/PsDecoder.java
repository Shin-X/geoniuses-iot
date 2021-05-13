package com.geoniuses.udp.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.IOException;
import java.util.List;

/**
 * Created  zyf in 2019/10/8 14:49
 */
public class PsDecoder  extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf in = msg.content();
        int length = 0;
        ByteBuf buf = null;
        for (int i = 0; i < in.readableBytes(); i++) {
            if (in.getByte(i) == 0x12 && in.getByte(i+1) == 0X34 && in.getByte(i+2)== 0x56){
                length = in.getUnsignedShort(i+3);
                buf = ctx.alloc().heapBuffer(length);
                in.getBytes(i, buf,length);
                break;
            }
        }
        System.out.println("buf===============> = " + buf);
        //SimpleChannelInboundHandler 它会自动进行一次释放(即引用计数减1). handler会调用SimpleChannelInboundHandler，所以先对其进行加1
        msg.content().retain();
        DatagramPacket replace = msg.replace(buf);
//        System.out.println("in = " + ByteBufUtil.hexDump(in));
        out.add(replace);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
        cause.printStackTrace();
    }
}
