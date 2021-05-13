package com.geoniuses.udp.pojo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created  zyf in 2019/10/11 9:02
 */
@Builder
@Data
public class InnerPsMessage  implements Serializable {

    //设备编码 1字节
    private String deviceCode;

    //功能码 固定值0x2c 2字节
    private int code;

    //保留位1 4字节
    private int reserve1;

    //保留位2 2字节
    private int reserve2;

    //记录数量 1字节
    private int record;

    //记录格式 此条报文存在哪些监测项 2字节
    private int item;

    //电池电压 2字节
    private double voltage;

    //现场状态 1字节
    private int status;

    //协议版本 1字节
    private int protocolVersion;

    //参数版本 1字节
    private int parametVersion;

    //信号质量 1字节
    private int signalQuality;

    //保留位3 3字节
    private int reserve3;

    //历史纪录（数据）
    private byte[] msgBody;
//    //历史纪录（数据）
//    private ByteBuf msgBody;

    //内层协议校验
    private int innerCrc;

}
