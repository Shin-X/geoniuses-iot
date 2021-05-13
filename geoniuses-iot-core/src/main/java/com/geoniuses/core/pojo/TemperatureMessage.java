package com.geoniuses.core.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @auther: zyf
 * @Date: 2020/09/15 16:57
 * @Description 温度数据
 */
@Builder
@Data
public class TemperatureMessage {
    private int status;
    private int voltage;
    private int sign;
    private int temperature;
    private int humidity;
    private Long sensorNo;
}
