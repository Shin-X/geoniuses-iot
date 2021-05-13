package com.geoniuses.mqtt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.geoniuses.mqtt.service.ParseMessageService;
import com.geoniuses.core.config.KafkaTopicConstant;
import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.kafka.ProcessPubSubData;
import com.geoniuses.core.mapper.TransferMapper;
import com.geoniuses.core.pojo.*;
import com.geoniuses.core.utils.CheckUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @auther: zyf
 * @Date: 2020/09/11 11:23
 * @Description
 */
@Service
@Slf4j
public class ParseMessageServiceImpl implements ParseMessageService {


    private ObjectMapper om = new ObjectMapper();

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private TransferMapper mapper;

    @Autowired
    private RedisClusterConfig redisClusterConfig;

    @Autowired
    private LinkedBlockingQueue queue;


    /**
     * 有害气体数据
     * @param byteBuf
     */
    private void doGasMessage(ByteBuf byteBuf) {

        log.debug("有害气体数据[{}]",ByteBufUtil.hexDump(byteBuf));
        //包头+通讯类型 2个字节+1个字节
         byteBuf.skipBytes(3);
         //设备唯一编号
        long sensorNo = byteBuf.readLong();
//        byteBuf.skipBytes(8);
        byte aByte = byteBuf.getByte(1);
        if (aByte == 0x00){
            System.out.println("00表示数据包");
        }
        //传感器个数
        byte b = byteBuf.readByte();
        //第一类传感器占用的字节数
        byte b1 = byteBuf.readByte();
        //传感器类型
        int type = byteBuf.readUnsignedMedium();
        if (type == 0X000087){
            //有害气体数据
        }
        int h2s = byteBuf.readUnsignedShort();
        h2s /= 1000;

        int o2 = byteBuf.readUnsignedShort();
        o2 /= 100;

        int co = byteBuf.readUnsignedShort();
        co /= 1000;

        float ch4 = byteBuf.readFloat();

        byte lq = byteBuf.readByte();
        if (lq == 0x00){
            //液位 正常 true为正常
        }else {
            //液位报警
        }
        //DS18B20
        int temperature = byteBuf.readUnsignedShort();

        temperature /=10;
        //倾斜报警
        byte slope = byteBuf.readByte();

        if (slope == 0x01){
            //倾斜报警
        }else {
            //0为正常 true为正常
        }

        //信号强度
        int sign = byteBuf.readByte();
        sign = sign * 2 - 113;

        //电池电量
        int battery = byteBuf.readUnsignedShort();

        battery /= 100;

        HarmGasMessage harmGasMessage = HarmGasMessage.builder()
                .battery(battery)
                .CH4(ch4)
                .CO(co)
                .H2S(h2s)
                .liquidlevel(lq == 0x00 ? true : false)
                .O2(o2)
                .sign(sign)
                .slope(slope == 0x00 ? true : false)
                .temperature(temperature)
                .sensorNo(sensorNo)
                .build();
        Map<String,Object> map =  om.convertValue(harmGasMessage, Map.class);
        Map<String,Object> harmGasMap = new HashMap<>();
        map.entrySet().forEach(m -> harmGasMap.put(HarmGasEnum.getValue(m.getKey()),m.getValue()));
        System.out.println("map = " + map);
        harmGasMap.put("sensorNo",sensorNo);
        harmGasMap.remove(null);
        harmGasMap.put("topic", KafkaTopicConstant.HARM_GAS_TOPIC);
        executor.execute(new ProcessData(harmGasMap, mapper,queue, redisClusterConfig));
        harmGasMap.put("pubSubTopic",KafkaTopicConstant.HARM_GAS_PUSH_TOPIC);
        executor.execute(new ProcessPubSubData(harmGasMap, queue));
    }

    /**
     * 井盖移位
     * @param byteBuf
     */
    private void doOpenMessage(ByteBuf byteBuf) {
        log.debug("井盖移位数据[{}]",ByteBufUtil.hexDump(byteBuf));
        if (!crc(byteBuf)){
            return;
        }
        //包头
        byteBuf.skipBytes(2);
        //通讯类型
        byteBuf.skipBytes(1);

        long sensorNo = byteBuf.readLong();
        //需要应答标志
        byte ack = byteBuf.readByte();
        if (ack == 0x01){
            //需要应答
        }
        byte b = byteBuf.readByte();
        if (b != 0x00){
            //不等于0是心跳包
            return;
        }
        //传感器个数
        byte b1 = byteBuf.readByte();

        //第一类传感器字节数
        byte sensorOne = byteBuf.readByte();

        int s2018 = byteBuf.readUnsignedMedium();

        int sign = byteBuf.readByte();

        sign = (sign * 2) - 113;

        //电压
        double voltage = byteBuf.readUnsignedShort();

        voltage = voltage / 4096 * 5;

        //水浸状态值 0为无水 1 为有水
        byte water = byteBuf.readByte();

        //状态 00 为恢复正常 01 报警 02 倾斜  03 上电第一次报数据
        byte status = byteBuf.readByte();

        //俯仰角
        double xPitch = byteBuf.readUnsignedShort();
        xPitch /= 100;
        //横滚角
        double zRoll = byteBuf.readUnsignedShort();
        zRoll /= 100;
        //水平夹角
        double levelAngle = byteBuf.readUnsignedShort();
        levelAngle /= 100;

        //第一个扩展字段
        byte extendOne = byteBuf.readByte();

        //第一个扩展字段字节数
        byte extendBytes = byteBuf.readByte();

        //第一个扩展字段类型
        byte extendType = byteBuf.readByte();

        long imsi = byteBuf.readLong();

        //第二个扩展字段字节数
        byte extendTwoBytes = byteBuf.readByte();

        //第二个扩展字段类型
        byte extendTwoType = byteBuf.readByte();
        byte[] bytes = new byte[6];
        byteBuf.readBytes(bytes);

        //第三个扩展字段字节数
        byte extendThreeBytes = byteBuf.readByte();

        //第三个扩展字段类型
        byte extendThreeType = byteBuf.readByte();

        //序列号
        int No = byteBuf.readUnsignedShort();

        //crc
        byteBuf.skipBytes(2);

        System.out.println("byteBuf剩余可读字节数 = " + byteBuf.readableBytes());

        OpenMessage openMessage = OpenMessage.builder()
                .levelAngle(levelAngle)
                .status(status)
                .sign(sign)
                .voltage(voltage)
                .waterOut(water == 0x00 ? true : false)
                .xPitch(xPitch)
                .zRoll(zRoll)
                .sensorNo(sensorNo)
                .build();

        Map<String,Object> map =  om.convertValue(openMessage, Map.class);
        Map<String,Object> openMap = new HashMap<>();
        map.entrySet().forEach(m -> openMap.put(OpenEnum.getValue(m.getKey()),m.getValue()));
        System.out.println("map = " + map);
        openMap.put("sensorNo",sensorNo);
        openMap.remove(null);
        openMap.put("topic",KafkaTopicConstant.OPEN_TOPIC);
        executor.execute(new ProcessData(openMap, mapper,queue, redisClusterConfig));
        openMap.put("pubSubTopic",KafkaTopicConstant.OPEN_PUSH_TOPIC);
        executor.execute(new ProcessPubSubData(openMap, queue));

    }

    /**
     * 温度 湿度
     * @param byteBuf
     */
    private void doTemperatureMessage(ByteBuf byteBuf) {

        log.debug("温度数据[{}]",ByteBufUtil.hexDump(byteBuf));
        if (!crc(byteBuf)){
            return;
        }
        //包头 2字节
        byteBuf.skipBytes(2);
        //通讯类型 1字节
        byteBuf.skipBytes(1);
        //设备IMEI
        long sensorNo = byteBuf.readLong();

        byte aByte = byteBuf.getByte(1);
        //TODO 如何应答？ 设备没有应答
        if (aByte == 0x01){
            //需要应答
        }
        //心跳包 00是数据包
        byte b = byteBuf.readByte();
        //不等于0为心跳包，不做处理
        if (b != 0x00){
            log.info("温度数据心跳包:{}",ByteBufUtil.hexDump(byteBuf));
//            byteBuf.skipBytes(byteBuf.readableBytes());
//            return;
        }
        //传感器数
        byte sensorNumber = byteBuf.readByte();
        //第一类传感器数据的字节数
        byte bytes1 = byteBuf.readByte();
        int s2022 = byteBuf.readMedium();
        if (s2022 == 0x000096){
            //数据为s2022协议
        }
        //霍尔传感器值   00 为断开 66 为吸合
        byte status = byteBuf.readByte();
        //电压高位
        int voltage = byteBuf.readShort();
        voltage = voltage * 5 /4096;
        //信号强度
        int signHigh = byteBuf.readByte();
        signHigh = signHigh * 2 - 113;
        //液位传感器
        byte ywSensorNo = byteBuf.readByte();
        //温度
        int temperature = byteBuf.readUnsignedShort();
        temperature *=0.0625;

        //湿度
        byte humidity = byteBuf.readByte();
        humidity /=256;
        //扩展字段个数
        int extendNumber = byteBuf.readByte();

        for (int i = 0; i < extendNumber - 1; i++) {
            byte extendOne = byteBuf.readByte();
            int extendOneType = byteBuf.readByte();
            System.out.println("扩展字段类型 = " + extendOneType);
            byte[] bytes = new byte[extendOne];
            byteBuf.readBytes(bytes);
            System.out.println("扩展字段"+i+"====>" + Integer.parseInt(new String(bytes)));
        }
        //包序号
        short i = byteBuf.readShort();
        //crc
        short i1 = byteBuf.readShort();

        System.out.println("byteBuf.readableBytes() = " + byteBuf.readableBytes());

        byteBuf.skipBytes(byteBuf.readableBytes());

        TemperatureMessage temperatureMessage = TemperatureMessage.builder()
                .humidity(humidity)
                .status(status == 0x00 ? 0 : 1)
                .temperature(temperature)
                .sign(signHigh)
//                .sensorNo(sensorNo)
                .voltage(voltage)
                .build();
        System.out.println("temperatureMessage = " + temperatureMessage);

        Map<String,Object> map =  om.convertValue(temperatureMessage, Map.class);
        Map<String,Object> temMap = new HashMap<>();
        map.entrySet().forEach(m -> temMap.put(TemperatureEnum.getValue(m.getKey()),m.getValue()));
        temMap.put("sensorNo",sensorNo);
        temMap.remove(null);
        temMap.put("topic", KafkaTopicConstant.TEMPERATURE_TOPIC);
        executor.execute(new ProcessData(temMap, mapper,queue, redisClusterConfig));
        temMap.put("pubSubTopic",KafkaTopicConstant.TEMPERATURE_PUSH_TOPIC);
        executor.execute(new ProcessPubSubData(temMap, queue));
        System.out.println("temMap = " + temMap);
    }



    /**
     * 液位水压
     * @param byteBuf
     */
    private void doLiquidLevelMessage(ByteBuf byteBuf) {
        log.debug("液位水压数据[{}]",ByteBufUtil.hexDump(byteBuf));
        byteBuf.skipBytes(3);

        long sensorNo = byteBuf.readLong();

        //应答
        byte ack = byteBuf.readByte();

        //心跳
        byte b = byteBuf.readByte();

        //传感器个数
        byte senserNumber = byteBuf.readByte();

        //传感器字节数
        byte sensorBytes = byteBuf.readByte();

        int type = byteBuf.readUnsignedMedium();

        if (type == 0x000105){
            //液位数据协议
        }

        byteBuf.skipBytes(1);

        //水位状态
        byte waterStatus = byteBuf.readByte();

        int voltage = byteBuf.readUnsignedShort();
        voltage /= 1000;
        //水位值
        int waterLevel = byteBuf.readUnsignedShort();
        waterLevel = (waterLevel - 300)/540;

        //信号强度
        int sign = byteBuf.readByte();
        sign = sign * 2 - 113;

        //井盖状态 00：正常  66：井盖拿走
        byte status = byteBuf.readByte();

        LiquidLevelMessage levelMessage = LiquidLevelMessage.builder()
//                .sensorNo(sensorNo)
                .sign(sign)
                .status(status == 0x00 ? true : false)
                .voltage(voltage)
                .waterLevel(waterLevel)
                .waterStaus(waterStatus)
                .build();
        Map<String,Object> map =  om.convertValue(levelMessage, Map.class);
        Map<String,Object> levelMap = new HashMap<>();
        map.entrySet().forEach(m -> levelMap.put(LiquidLevelEnum.getValue(m.getKey()),m.getValue()));
        System.out.println("levelMap = " + map);
        levelMap.put("sensorNo",sensorNo);
        levelMap.remove(null);
        levelMap.put("topic",KafkaTopicConstant.LIQUID_LEVEL_TOPIC);
        executor.execute(new ProcessData(levelMap, mapper,queue, redisClusterConfig));
        levelMap.put("pubSubTopic",KafkaTopicConstant.LIQUID_LEVEL_PUSH_TOPIC);
        executor.execute(new ProcessPubSubData(levelMap, queue));
    }


    /**
     * 数据校验
     * @param buf
     * @return
     */
    private boolean crc(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes() - 2];
        buf.getBytes(0,bytes);
        String str = ByteBufUtil.hexDump(buf);
        String crc = str.substring(str.length() - 4, str.length()).toUpperCase();
        String s = CheckUtil.mqttCrc(bytes);
        if (s.endsWith(crc)) {
            log.info("计算校验值：{}，数据包校验位，{}",s,crc);
            return true;
        }else {
            log.error("校验错误 --计算校验值：{}，数据包校验位，{}",s,crc);
            return false;
        }
    }


    @Override
    public void doMessage(MqttPublishMessage msg) {
        ByteBuf byteBuf = msg.content();
        ByteBuf copy = byteBuf.copy();
        byteBuf.skipBytes(15);
        int type = byteBuf.readUnsignedMedium();
        switch (type){
            case 0X000096:
                doTemperatureMessage(copy);
                break;
            case 0X000087:
                doGasMessage(copy);
                break;
            case 0X000084:
                doOpenMessage(copy);
                break;
            case 0X000105:
                doLiquidLevelMessage(copy);
                break;
            default:
                break;
        }
        byteBuf.skipBytes(byteBuf.readableBytes());
    }


}
