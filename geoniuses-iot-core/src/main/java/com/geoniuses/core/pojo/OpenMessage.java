package com.geoniuses.core.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @auther: zyf
 * @Date: 2020/09/16 11:14
 * @Description 井盖倾角数据
 */
@Builder
@Data
public class OpenMessage {

    private long sensorNo;
    private double voltage;
    private int sign;
    private boolean waterOut;
    //00 为恢复正常 01 报警 02 倾斜  03 上电第一次报数据
    /**
     * 0:正常
     * 1:报警；大于30度
     * 2.倾斜；15到30度
     * 3.上电第一次上报数据
     */
    private int status;
    //俯倾角
    private double xPitch;
    //横滚角
    private double zRoll;
    //水平夹角
    private double levelAngle;
}
