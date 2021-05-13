package com.geoniuses.tcp;


import com.geoniuses.core.enums.ProcotolConstant;
import com.geoniuses.core.enums.ProtocolEnum;
import com.geoniuses.core.pojo.water.pojo.WaterMessage;
import com.geoniuses.core.pojo.water.pojo.WaterQuality;
import com.geoniuses.core.pojo.water.pojo.WaterSample;
import com.geoniuses.core.utils.CheckUtil;
import com.geoniuses.core.utils.DateUtil;
import com.geoniuses.tcp.service.PluginService;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.util.Map;


/**
 * @Author zyf
 * @Date 2019/10/25 10:36
 */
//@ChannelHandler.Sharable

public class Handler extends SimpleChannelInboundHandler<ByteBuf> {


    //private ProcotolService procotolService;
   // public Handler(ProcotolService procotolService) {
       // this.procotolService = procotolService;
  //  }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException {
        String s = ByteBufUtil.hexDump(msg);
        int length = msg.readableBytes();
        if (length==24){
                //获取一个池化的bytebuf
               /* ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
                buffer.writeBytes("ACK".getBytes());
                ctx.channel().writeAndFlush(buffer);*/

                //设置相应的协议
             //   ctx.channel().attr(AttributeKey.valueOf(ProcotolConstant.TERMINAL_TYPE)).set("MODBUS");
                //匹配不通协议
        //        procotolService.channelRead(ctx, msg);


                    //数据包解析
                    URL[] urls = new URL[1];
                    urls[0] = new URL("file:" + "D:\\PluginA-1.0-SNAPSHOT.jar");
                    URLClassLoader urlClassLoader = new URLClassLoader(urls);
                    Class<?> aClass = urlClassLoader.loadClass("com.geoniuses.tcp.service.PluginAimpl");
                    Object instance = aClass.newInstance();
                    Map<String, Object> dataByForm = ((PluginService) instance).parser(msg);
                    System.out.println(dataByForm);

                    // Map<String, Object> dataByForm = ParserWellCoverUtil.parser((ByteBuf) data);
                    //是否将设备添加到黑名单
          /*  new Thread(() -> {
                ProtocolContext protocolContext = new ProtocolContext();
                protocolContext.setContext(ctx);
                protocolContext.setObject(data);
                deviceAuthService.addDeviceToBlackList(protocolContext,dataByForm.get("sensorNo").toString());
            }).start();*/
                    //TODO 过滤
//            boolean filter = manholeCoverLogic(dataByForm);
                    boolean filter = true;
                    dataByForm.put("DT", DateUtil.getNowDateSync());
                    System.err.println(dataByForm);

        }else if(length==33){
            WaterMessage waterMessage = translateToPackage(msg);
            answer(ctx, waterMessage);

            System.err.println(waterMessage);
        }else{
            ctx.channel().close();
            return;
        }
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


    private WaterMessage translateToPackage(ByteBuf payload) {
        if (payload.readableBytes() == 0)
            return null;
        WaterMessage decode = decode(payload);
        return decode;
    }
    private static final int SAMPLE_CODE = 0XC9;
    private static final int WATER_CODE = 0XC8;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private WaterMessage decode(ByteBuf in) {
        System.out.println("in = " + in);
        System.out.println(ByteBufUtil.hexDump(in));
        //跳过头字节和长度字节
        in.skipBytes(2);
        //指令标识码
        int identifier = in.readUnsignedByte();
        WaterMessage waterMessage = null;
        switch (identifier){
            case  SAMPLE_CODE:
                return sampleData(in);
            case  WATER_CODE:
                return waterQuality(in);
            default:
                break;
        }
        return waterMessage;
    }

    /**
     * 水质因子上传
     * @param in
     */
    private WaterMessage waterQuality(ByteBuf in) {
        //仪表地址
        byte[] address = new byte[3];
        in.readBytes(address);
        String sensorNo = ByteBufUtil.hexDump(address);

        //瞬时流量，现场通过4~20mA读取的流量计瞬时流量值，*10上传，表示带1位小数
//        float flowRate = in.readUnsignedShort() /10;
        int flowRateInt = in.readUnsignedShort();
        int CODInt = in.readUnsignedShort();
        int SSInt = in.readUnsignedShort();
        int PHInt = in.readUnsignedShort();
        int NH3Int = in.readUnsignedShort();
        int PInt = in.readUnsignedShort();

//        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        //瞬时流量，现场通过4~20mA读取的流量计瞬时流量值，*10上传，表示带1位小数
        String flowRate = df.format((float)flowRateInt/10);
        //实时UV值（COD值），*10上传，表示带1位小数
        String COD = df.format((float) CODInt / 10);
        //实时SS值，*10上传，表示带1位小数
        String SS = df.format((float) SSInt / 10);
        //实时PH值，*100上传，表示带2位小数
        String PH = df.format((float) PHInt / 100);
        //实时氨氮值，*100上传，表示带2位小数
        String NH3 = df.format((float)NH3Int/100);
        //实时总磷值，*100上传，表示带2位小数
        String P = df.format((float)PInt/100);
        //累积流量，4字节32位无符号长整形
        long totalFlow = in.readUnsignedInt();

        //保留位1
        in.readShort();
        //保留位2
        in.readShort();

        //时间
        byte[] bytes = new byte[6];
        in.readBytes(bytes);

        String date = CheckUtil.bcd2String(bytes);
        while (date.length() < 12){
            date = "0" + date;
        }
        String second = date.substring(0, 2);
        String minute = date.substring(2, 4);
        String hour = date.substring(4, 6);
        String day = date.substring(6, 8);
        String month = date.substring(8, 10);
        String year = date.substring(10, 12);
        System.out.println("时间 = " + date);
        //帧尾 0xa5
        short i = in.readUnsignedByte();

        WaterQuality waterQuality = WaterQuality.builder()
                .sensorNo(sensorNo)
                .address(address)
                .flowRate(flowRate)
                .COD(COD)
                .SS(SS)
                .PH(PH)
                .NH3(NH3)
                .P(P)
                .totalFlow(totalFlow)
                .date("20"+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second)
                .build();

        System.out.println("waterQuality = " + waterQuality);

        return waterQuality;
    }

    /**
     * 仪器留样记录上传
     * @param in
     */
    private WaterMessage sampleData(ByteBuf in) {
        //仪表地址
//      int address = in.readUnsignedMedium();
        byte[] address = new byte[3];
        in.readBytes(address);
        String sensorNo = ByteBufUtil.hexDump(address);
        //仪器报警
        int alarm = in.readUnsignedByte();

        int x = 0;
        int[] alarm1 = {0,0,0,0,0,0,0,0};
        for (int i = 0; i < 8; i++) {
            if ((alarm >> i) % 2 ==1){
                alarm1[i] = 1;
            }
        }
        //仪表报警第八位保留
        int reserve1 = alarm1[7];
        //仪表报警第七位保留
        int reserve2 = alarm1[6];
        //仪表报警第六位 仪器是否启动，0空闲，1启动
        int start = alarm1[5];
        //仪表报警第五位保留
        int reserve3 = alarm1[4];
        //仪表报警第4位 仪器电源状态，0代表供电正常，1代断电
        int power = alarm1[3];
        //仪表报警第3位 排放口液位信号 0代表无液位，停止排水 1代表有液位，开始排水
        int liquidLevelSignal = alarm1[2];
        //仪表报警第2位 仪器前一次采样的进水状态，0代表进水成功，1代表进水失败
        int firstWater = alarm1[1];
        //仪表报警第1位 仪器门状态，0代表关，1代表开
        int instrumentWicket = alarm1[0];

        //水箱温度
        int cisternTem =in.readUnsignedByte();
        //正温度
        int tem = 1;
        //如果二进制的第八位等于1那么温度为负温度，如果为零说明为正温度
        if (cisternTem >> 7 == 1){
            tem = -1;
        }

//        触发采样时的UV值
        int UVInt = in.readUnsignedShort();
        String UV = df.format((float)UVInt/10);

        //触发采样时的SS值，
        int SSInt = in.readUnsignedShort();
        String SS = df.format((float)SSInt/10);

        //触发采样时的NH3值
        int NH3Int = in.readUnsignedShort();
        String NH3 = df.format((float)NH3Int/100);

        //触发采样时的PH值
        int PHInt = in.readUnsignedShort();
        String PH = df.format((float)PHInt/100);
        //采样瓶号
        int sampleBottle = in.readUnsignedByte();

        //采样瓶次
        int sampleBottleCount = in.readUnsignedShort();

        //本次采样量
        int sampleQuantity = in.readUnsignedShort();

        //当前瓶水样总容量
        int sampleTotalCapacity = in.readUnsignedShort();

        //触发采样时的温度值，2字节，整型数，*100上传
        int sampleTem = in.readUnsignedShort();
        sampleTem = sampleTem/100 * cisternTem;

        //0xfe模拟触发采样记录，0xff其他模式采样记录
        int sampleRecord = in.readUnsignedByte();

        //时间
        byte[] bytes = new byte[6];
        in.readBytes(bytes);

        String date = CheckUtil.bcd2String(bytes);
        if (date.length() < 12){
            date = 0 + date;
        }
        System.out.println("时间 = " + date);

        String second = date.substring(0, 2);
        String minute = date.substring(2, 4);
        String hour = date.substring(4, 6);
        String day = date.substring(6, 8);
        String month = date.substring(8, 10);
        String year = date.substring(10, 12);

        System.out.println("日期 = " + year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
        //帧尾 0xa5
        int endCode = in.readUnsignedByte();

        WaterSample waterSample = WaterSample.builder()
                .sensorNo(sensorNo)
                .address(address)
                .alarm(alarm)
                .cisternTem(cisternTem)
                .date("20"+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second)
                .firstWater(firstWater)
                .instrumentWicket(instrumentWicket)
                .liquidLevelSignal(liquidLevelSignal)
                .NH3(NH3)
                .PH(PH)
                .power(power)
                .reserve1(reserve1)
                .reserve2(reserve2)
                .reserve3(reserve3)
                .sampleRecord(sampleRecord)
                .sampleBottle(sampleBottle)
                .sampleBottleCount(sampleBottleCount)
                .sampleQuantity(sampleQuantity)
                .sampleTem(sampleTem)
                .sampleTotalCapacity(sampleTotalCapacity)
                .start(start)
                .UV(UV)
                .SS(SS)
                .build();

        System.out.println("waterSample = " + waterSample);
        return waterSample;
    }

    private void answer(ChannelHandlerContext ctx, WaterMessage request) {
        ByteBuf buffer = Unpooled.buffer();
        if (request instanceof WaterQuality){
            WaterQuality waterQuality = (WaterQuality) request;
            buffer.writeByte(0XAA);
            buffer.writeByte(0X07);
            buffer.writeByte(0XCD);
            byte[] address = waterQuality.getAddress();
            buffer.writeBytes(address);
            buffer.writeByte(0XA5);
        }
        ctx.channel().writeAndFlush(buffer);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        ctx.channel().attr(AttributeKey.valueOf(ProcotolConstant.TERMINAL_TYPE)).set(ProtocolEnum.MODBUS);
      //  procotolService.onConnect(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //procotolService.onDisconnect(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent event = (IdleStateEvent) evt;
           // procotolService.triggered(event.state(), ctx);
        }
    }


}
