package com.geoniuses.tcp;

import com.geoniuses.core.utils.CheckUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author zyf
 * @Date 2019/10/25 9:49
 */
public class TCPDecoder extends ByteToMessageDecoder {


    private static final int ManholecoverHEAD = 0XE0;
    private static final int ManholecoverEND = 0X0A;

    private static final int WaterHEAD = 0xAA;
    private static final int WaterEND = 0xA5;



    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){
//        System.out.println("in =============> " + in);
        int length = in.readableBytes();
        //井盖数据为24位
        if (length == 24){
            ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer();
            String s = ByteBufUtil.hexDump(in);
            if (crc(s)) {
                //获取一个池化的bytebuf
                //e0 01 002f027c3f41 00000059 0000 01 00 0010 4f 20 08 05 f4 0a
                ByteBuf OutBuffer = UnpooledByteBufAllocator.DEFAULT.buffer();
                OutBuffer.writeBytes("ACK".getBytes());
                ctx.channel().writeAndFlush(OutBuffer);
            }
            for (int i = 0; i < length; i++) {
                if (in.getUnsignedByte(i) == ManholecoverHEAD && in.getUnsignedByte(i+length-1) == ManholecoverEND){
                    short head = in.getUnsignedByte(i);
                    short end = in.getUnsignedByte(i + length - 1);
                    in.readBytes(i);
                    in.readBytes(buf,length);
                    break;
                }
            }
//        String hexDump = ByteBufUtil.hexDump(buf);
//        System.out.println("hexDump ===========> " + hexDump);
//        buf.skipBytes(buf.readableBytes());
            out.add(buf);
        } else if (length == 33){
            //aa 1e c9 03 02 01 44 30 00 96 00 4a ff ff 01 90 02 00 00 00 64 00 00 00 00 fe 33 01 09 16 01 19 a5
            ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer();
            for (int i = 0; i < length; i++) {

                short head = in.getUnsignedByte(i);
                short end = in.getUnsignedByte(i + length - 1);

                if (in.getUnsignedByte(i) == WaterHEAD && in.getUnsignedByte(i+length-1) == WaterEND){
                    in.readBytes(i);
                    in.readBytes(buf,length);
                    break;
                }
            }
//        String hexDump = ByteBufUtil.hexDump(buf);
//        System.out.println("hexDump ===========> " + hexDump);
//        buf.skipBytes(buf.readableBytes());
            out.add(buf);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().flush().close();
//        ctx.channel().close();
    }



    private boolean crc(String msg) {
        String substring = msg.substring(0, msg.length() - 4);
        String crc = CheckUtil.makeChecksum(substring);
        String substring1 = msg.substring(msg.length() - 4, msg.length() - 2);
        if (substring1.equalsIgnoreCase(crc)){

            return true;
        }
        return false;
    }
}
