package com.geoniuses.tcp.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author zyf
 * @Date 2019/10/25 9:49
 */
public class ManholeCoverDecoder extends ByteToMessageDecoder {

    private static final int LENGTH = 24;
    private static final int HEAD = 0XE0;
    private static final int END = 0X0A;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){
//        System.out.println("in =============> " + in);
        int length = in.readableBytes();
        //井盖数据为24位
        if (length < LENGTH){
            return;
        }
        ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < length; i++) {
            if (in.getUnsignedByte(i) == HEAD && in.getUnsignedByte(i+LENGTH-1) == END){
                in.readBytes(i);
                in.readBytes(buf,LENGTH);
                break;
            }
        }
//        String hexDump = ByteBufUtil.hexDump(buf);
//        System.out.println("hexDump ===========> " + hexDump);
//        buf.skipBytes(buf.readableBytes());
        out.add(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().flush().close();
//        ctx.channel().close();
    }
}
