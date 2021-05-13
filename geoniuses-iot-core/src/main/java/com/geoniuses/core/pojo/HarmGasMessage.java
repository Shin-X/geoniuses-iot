package com.geoniuses.core.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @auther: zyf
 * @Date: 2020/09/16 08:27
 * @Description 有害气体
 */
@Data
@Builder
public class HarmGasMessage {
    private float H2S;
    private float O2;
    private float CO;
    private float CH4;

    private boolean liquidlevel;

    private double temperature;
    private boolean slope;

    private int sign;

    private int battery;
    private long sensorNo;
}
