package com.geoniuses.udp;


import com.alibaba.fastjson.JSONObject;
import com.geoniuses.core.config.KafkaConfiguration;
import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.config.ThreadPoolConfig;
import com.geoniuses.core.enums.ProcotolConstant;
import com.geoniuses.core.enums.ProtocolEnum;
import com.geoniuses.core.enums.SensorItemConstant;
import com.geoniuses.core.kafka.ProcessPubSubData;
import com.geoniuses.core.mapper.TransferMapper;
import com.geoniuses.core.pojo.ParseMysqlToSyncTopic;
import com.geoniuses.core.pojo.ProcessData;
import com.geoniuses.core.pojo.water.pojo.WaterMessage;
import com.geoniuses.core.pojo.water.pojo.WaterQuality;
import com.geoniuses.core.server.DataSourceFactory;
import com.geoniuses.core.utils.CheckUtil;
import com.geoniuses.udp.pojo.InnerPsMessage;
import com.geoniuses.udp.pojo.PsMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created  zyf in 2019/10/8 14:50
 */

@Component
public class PsHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final String strDate = "2000-01-01 00:00:00";
    private static final int closeOrder=0x80;
    private static final int closeFunCode=0x34;
    private Map<String,Object> map;

    private DataSourceFactory dataSourceFactory;



    public PsHandler(  Map<String,Object> map,DataSourceFactory dataSourceFactory) {
        this.map = map;
        this.dataSourceFactory=dataSourceFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket data) throws Exception {
        if (data != null){
            ctx.channel().attr(AttributeKey.valueOf(ProcotolConstant.TERMINAL_TYPE)).set(ProtocolEnum.PS);
            DatagramPacket copy = data.copy();
            ByteBuf content = copy.content();
            int i = content.refCnt();
            System.out.println("-------------------------------> " + i);
            System.out.println("ByteBufUtil.hexDump(content) = " + ByteBufUtil.hexDump(content));
           // procotolService.channelRead(ctx,copy);


            PsMessage msg = translateToPackage(data);

            InetSocketAddress inetSocketAddress = msg.getInetSocketAddress();
            System.out.println("inetSocketAddress = " + inetSocketAddress);
            //数据条数
            int record = msg.getInnerPsMessage().getRecord();

            double voltage = msg.getInnerPsMessage().getVoltage();

            //数据体
            byte[] msgBody = msg.getInnerPsMessage().getMsgBody();

            //设备编号
            String deviceCode = msg.getInnerPsMessage().getDeviceCode();

            //监测项
            int item = msg.getInnerPsMessage().getItem();

            //封装包
            packageData(voltage,record,deviceCode,item, Unpooled.copiedBuffer(msgBody), "PS");

            ByteBuf byteBuf = encodeData(msg);
            DatagramPacket datagramPacket = new DatagramPacket(byteBuf,inetSocketAddress);
            //应答
            ctx.channel().writeAndFlush(datagramPacket);
        }
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
    private PsMessage translateToPackage(DatagramPacket payload) {
        PsMessage psMessage = null;
        psMessage = decodeData(payload.content());
        if (psMessage == null) {
            return null;
        }
        psMessage.setInetSocketAddress(payload.sender());
        return psMessage;
    }
    /**
     * 封装数据
     * @param in
     * @return
     * @throws ParseException
     */
    private PsMessage decodeData(ByteBuf in) {
        //系统识别码默认为0x123465 3字节
        int headCode = in.readUnsignedMedium();

        //数据包长度 2字节
        int length = in.readUnsignedShort();
        //包序号 1
        int orderNum = in.readUnsignedByte();
        //数据类型  编码为34说明是结束包  1字节
        int funCode = in.readUnsignedByte();
        //等于0x34说明是设备休眠应答包
        if (funCode == 0x34){
            System.out.println("funCode = "+funCode +"设备休眠应答========》" + ByteBufUtil.hexDump(in));
            return null;
        }
        //源地址长度 1
        int sourcelength = in.readUnsignedByte();

        byte[] sourceAddress = new byte[6];
        //源地址 6
        in.readBytes(sourceAddress);
        String sensorNo = ByteBufUtil.hexDump(sourceAddress);
        if (sourcelength == 0x0b){
            sensorNo = sensorNo.substring(0,sensorNo.length()-1);
        }
        //目标地址长度与源地址长度相同 1
        short dstLength = in.readUnsignedByte();

        byte[] dstAddress = new byte[6];
        //目的地址 6
        in.readBytes(dstAddress);


        //======================================内层协议开始


        //设备编码 1
        int deviceCode = in.readUnsignedByte();

        //功能码 固定值0x2c 2
        int code = in.readUnsignedByte();

        //保留位1 4
        int reserve1 = new Long(in.readUnsignedInt()).intValue();
        //保留为2 2
        int reserve2 = in.readUnsignedShort();


        //记录数量 1
        int record = in.readUnsignedByte();

        //查看记录格式 （此条报文存在哪些监测项） 2   32
        int item = in.readUnsignedShort();
        //电池电压 2
        double batteryVoltage = in.readUnsignedShort();
        batteryVoltage = batteryVoltage / 100;
        //现场状态 最后一包 1字节
        int status = in.readUnsignedByte();

        //协议版本 1
        short protocolVersion = in.readUnsignedByte();
        //参数版本 向上位机标示参数版本号 1
        short parametVersion = in.readUnsignedByte();

        //信号质量  范围0-31,99 为未检测到信号质量 1
        short signalQuality = in.readUnsignedByte();

        //保留三位 3  41
        int reserve3 = in.readUnsignedMedium();

        //历史记录数据长度，总长度减去前面字节数并减去后面三位校验位
        int dataLength = length - 41 - 3;
        //将从readerIndex到datalength长度的数据读到dataBuffer
        byte[] dataBytes = new byte[dataLength];
        in.readBytes(dataBytes);

        //内层协议校验
        int innerCrc = in.readUnsignedShort();
        //内层协议
        InnerPsMessage innerPsMessage = InnerPsMessage.builder()
                .deviceCode(sensorNo)
                .code(code)
                .reserve1(reserve1)
                .reserve2(reserve2)
                .record(record)
                .item(item)
                .voltage(batteryVoltage)
                .status(status)
                .protocolVersion(protocolVersion)
                .parametVersion(parametVersion)
                .signalQuality(signalQuality)
                .reserve3(reserve3)
                .msgBody(dataBytes)
                .innerCrc(innerCrc)
                .build();

        //外层校验码
        int outerCrc = in.readUnsignedByte();

        PsMessage psMessage = PsMessage.builder()
                .headCode(headCode)
                .length(length)
                .orderNum(orderNum)
                .funCode(funCode)
                .sourcelength(sourcelength)
                .sourceAddress(sourceAddress)
                .dstLength(dstLength)
                .dstAddress(dstAddress)
                .innerPsMessage(innerPsMessage)
                .outerCrc(outerCrc).build();

        return psMessage;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println("socketAddress = " + socketAddress);
     //   ctx.channel().attr(AttributeKey.valueOf(ProcotolConstant.TERMINAL_TYPE)).set(ProtocolEnum.PS);
      //  procotolService.onConnect(ctx);
    }
//
    /**
     * 封装包，发送kafka并更新mysql
     * @param record
     * @param deviceCode
     * @param item  记录格式
     * @param msgBody
     * @throws ParseException
     */
    private void packageData(double voltage, int record, String deviceCode, int item, ByteBuf msgBody,String terminalType){
        List<Map<String, Object>> dataList = new ArrayList();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        ThreadPoolConfig threadPoolConfig = (ThreadPoolConfig) (dataSourceFactory.createDataSource("thread"));
        TransferMapper transferMapper = (TransferMapper) (dataSourceFactory.createDataSource("sql"));
        LinkedBlockingQueue queue = ((KafkaConfiguration) (dataSourceFactory.createDataSource("kafka"))).queue();
        RedisClusterConfig redisClusterConfig = (RedisClusterConfig) (dataSourceFactory.createDataSource("redis"));
        try {
            startDate = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //遍历记录数量 查看收到几条历史记录
        for (int i = 0; i < record; i++) {
            //封装监测项
            Map<String,Object> dataMap = new HashMap<>();
            //读取每条数据的时间 4字节
            Long time = msgBody.readUnsignedInt();
            if (time.toString().length() == 9){
                time = time * 1000;
            }else if (time.toString().length() == 10){
                time = time * 100;
            }
            Date date= new Date(startDate.getTime()+time);
            //封装时间
//            dataMap.put("DATETIME","ok");
            dataMap.put("DT",simpleDateFormat.format(date));
            dataMap.put("sensorNo",deviceCode+"");
            dataMap.put(SensorItemConstant.PS_BATTERY_VOLTAGE+"",voltage+"");
            //根据协议文档 将记录格式转成二进制 如果是1则代表有此监测项
            String str = Integer.toBinaryString(item);
            //两个字节，不够十六位补零
            while (str.length() < 16){
                str = "0" + str;
            }
            //反转二进制字符串，顺序读取
            String strReverse = new StringBuffer(str).reverse().toString();
            for (int j = 0; j < strReverse.length(); j++) {
                String  s = strReverse.charAt(j)+"";
                if (s.equals("1")){
                    if (j == 11){ //二进制的第十一位位开关量，根据协议文档需要将开关量需要转换成二进制
                        //读取四个四个字节 开关量
                        long onOff = msgBody.readUnsignedInt();
                        //四个字节二进制  0001 0000 0000 0000 0000 0000 0010 0000
                        String onOffReverse = new StringBuffer(Long.toBinaryString(onOff)).reverse().toString();
                        for (int k = 0; k < onOffReverse.length(); k++) {
                            String rev = onOffReverse.charAt(k)+"";
                            if (rev.equals("1")){
                                dataMap.put(SensorItemConstant.items[j] +k+"","1");
                            }
//                            else {
//                                dataMap.put(SensorItemConstant.items[j] +k+"","0");
//                            }
                        }
                    }else {
                        //如果不是开关量，其他都为四个字节的数据

                        if ("8".equals(SensorItemConstant.items[j])){
                            //16表示是否缩放，00表示正常，01表示仪表电池电压低于3.37V
                            if (msgBody.readFloat() == 1601){
                                dataMap.put(SensorItemConstant.items[j] +"","1");
                            }else {
                                dataMap.put(SensorItemConstant.items[j] +"","0");
                            }
                        }
                        /**
                         * 有个事说下 积水点的液位数据有个误差（不固定）
                         * 一直在设备端消除不了
                         * 希望你在数据接收端坐下处理
                         * 小于0.5厘米的、负数的都显示为0
                         */
                        if ("11".equals(SensorItemConstant.items[j]) && msgBody.readFloat() < 0.5){
                            dataMap.put(SensorItemConstant.items[j] +"","0");
                        }
                        dataMap.put(SensorItemConstant.items[j] +"",msgBody.readFloat()+"");
                    }
                }
            }
            dataList.add(dataMap);
        }



        if (!CollectionUtils.isEmpty(dataList)){
            dataList.forEach(data ->{
                //发送到kafka
                String jsonStr = new JSONObject(data).toJSONString();
                if ("FW4100".equals(terminalType)){
//                    System.out.println("FW4100发送到kafka的数据===============》" + jsonStr);
//                    data.put("topic",FW4100Server.protocol_code);
////                    kafkaTemplate.send("ZYDL_FW4100",jsonStr);
////                    kafkaTemplate.send(FW4100Server.protocol_code,jsonStr);
//                    poolTaskExecutor.execute(new ProcessData(data,transferMapper,deque,redisTemplateConfig));
//                    poolTaskExecutor.execute(new ProcessData(data,transferMapper,deque));
                }


                if ("PS".equals(terminalType)){
                    System.out.println("PS发送到kafka的数据===============》" + jsonStr);
                    data.put("topic","ZYDL_Water03");
                    threadPoolConfig.threadPoolExecutor().execute(new ProcessData(data,transferMapper,queue , redisClusterConfig));
                }

                //发布订阅线程池
                data.put("pubSubTopic", "pub_Water03");
                ((ThreadPoolConfig)(dataSourceFactory.createDataSource("thread"))).pubSubPoolExecutor().execute(new ProcessPubSubData(data, ((KafkaConfiguration)(dataSourceFactory.createDataSource("kafka"))).queue()));
            });
            threadPoolConfig.threadPoolExecutor().execute(new ParseMysqlToSyncTopic(dataList.get(dataList.size()-1), transferMapper, queue, redisClusterConfig));
        }
    }
    /**
     * 封装应答数据
     * @param message
     * @return
     */
    private ByteBuf encodeData(PsMessage message)  {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        //默认值123456
        buffer.writeMedium(message.getHeadCode());
        //报文长度 初始值
        buffer.writeShort(0);
        int status = message.getInnerPsMessage().getStatus();
        int orderNum = message.getOrderNum();
        int funCode = message.getFunCode();
        int sourcelength = message.getSourcelength();
        byte[] sourceAddress = message.getSourceAddress();
        byte[] dstAddress = message.getDstAddress();
        int dstLength = message.getDstLength();

        //查看是否为最后一包
        if ((status >> 3) == 1){
            buffer.writeByte(closeOrder);
            buffer.writeByte(closeFunCode);
        }else{
            buffer.writeByte(orderNum);
            buffer.writeByte(funCode);
        }
        buffer.writeByte(sourcelength);
        buffer.writeBytes(dstAddress);
        buffer.writeByte(dstLength);
        buffer.writeBytes(sourceAddress);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long time = (System.currentTimeMillis() - startDate.getTime()) /1000;

        //查看是否为最后一包
        if ((status >> 3) == 1) {
            buffer.writeInt(time.intValue());
        }
        //写入校验初始值
        buffer.writeByte(0);
        //设置回应格式
        String s = ByteBufUtil.hexDump(buffer);
        int length1 = s.length();

        //回应报文长度
        int bufferLength = length1/2;
        buffer.setShort(3,bufferLength);
        String s1=  ByteBufUtil.hexDump(buffer);

        //异或校验码
        byte[] bytes = ByteBufUtil.getBytes(buffer);
        Integer decode = Integer.decode("0x"+ CheckUtil.getBCC(bytes));
        //设置异或校验码
        buffer.setByte(bufferLength-1,decode);

        return buffer;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      //  procotolService.onDisconnect(ctx);
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
