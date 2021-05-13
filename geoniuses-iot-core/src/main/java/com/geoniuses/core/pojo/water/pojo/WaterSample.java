package com.geoniuses.core.pojo.water.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author zyf
 * @Date 2019/10/18 11:50
 *
 * 仪器留样记录上传指令
 */
@Builder
@Data
public class WaterSample extends WaterMessage{

    //仪表地址
    private byte[] address;
    //仪表地址
    private String sensorNo;

    //仪器报警
    private int alarm;

    //仪表报警第八位保留
    private int reserve1;

    //仪表报警第七位保留
    private int reserve2 = 0;

    //仪表报警第六位 仪器是否启动，0空闲，1启动
    private int start = 0;

    //仪表报警第五位保留
    private int reserve3 = 0;

    //仪表报警第4位 仪器电源状态，0代表供电正常，1代断电
    private int power = 0;

    //仪表报警第3位 排放口液位信号 0代表无液位，停止排水 1代表有液位，开始排水
    private int liquidLevelSignal = 0;

    //仪表报警第2位 仪器前一次采样的进水状态，0代表进水成功，1代表进水失败
    private int firstWater = 0;

    //仪表报警第1位 仪器门状态，0代表关，1代表开
    private int instrumentWicket = 0;

    //水箱温度
    private int cisternTem;

    //触发采样时的UV值
    private String UV;

    //触发采样时的SS值，
    private String SS;

    //触发采样时的NH3值
    private String NH3;

    //触发采样时的PH值
    private String PH;

    //采样瓶号
    private int sampleBottle;

    //采样瓶次
    private int sampleBottleCount;

    //本次采样量
    private int sampleQuantity;

    //当前瓶水样总容量
    private int sampleTotalCapacity;

    //触发采样时的温度值，2字节，整型数，*100上传
    private int sampleTem;

    //0xfe模拟触发采样记录，0xff其他模式采样记录
    private int sampleRecord;

    //时间
    private String date;
}
