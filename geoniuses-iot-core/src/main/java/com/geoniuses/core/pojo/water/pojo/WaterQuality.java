package com.geoniuses.core.pojo.water.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author zyf
 * @Date 2019/10/18 11:54
 *
 * 仪器水质因子实时值上传指令
 */
@Builder
@Data
public class WaterQuality extends WaterMessage{

    //仪表地址
    private byte[] address;
    //仪表地址 设备编号
    private String sensorNo;

    //瞬时流量，现场通过4~20mA读取的流量计瞬时流量值，*10上传，表示带1位小数
    private String flowRate;

    //实时UV值（COD值），*10上传，表示带1位小数
    private String COD;

    //实时SS值，*10上传，表示带1位小数 浊度
    private String SS;

    //实时PH值，*100上传，表示带2位小数
    private String PH;

    //实时氨氮值，*100上传，表示带2位小数
    private String NH3;

    //实时总磷值，*100上传，表示带2位小数
    private String P;

    //累积流量，4字节32位无符号长整形
    private long totalFlow;

    //时间
    private String date;

}
