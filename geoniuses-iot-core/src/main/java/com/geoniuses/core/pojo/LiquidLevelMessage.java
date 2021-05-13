package com.geoniuses.core.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @auther: zyf
 * @Date: 2020/09/16 13:54
 * @Description 液位水压数据
 */
@Builder
@Data
public class LiquidLevelMessage {

    private long sensorNo;
    //01低水位，02正常，03高水位
    private int waterStaus;
    private float voltage;
    private double waterLevel;
    private int sign;
    //井盖状态 00：正常  66：井盖拿走
    private boolean status;
}
