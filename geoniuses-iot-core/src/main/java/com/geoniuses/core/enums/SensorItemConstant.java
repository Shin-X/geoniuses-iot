package com.geoniuses.core.enums;

/**
 * Created by ZYF on 2019/9/6.
 * <p>
 * 所有传感器的监测项常量表,用于封装json发送kafka
 * 对应监测项中的数据位字段iot_config中的order_num
 */
public class SensorItemConstant {


//    =================================================井盖===================================================================
    /**
     * 井盖信号
     */
    public static final String MANHOLE_COVER_SINGAL = "11";
    /**
     * 井盖数据包类型
     */
    public static final String MANHOLE_COVER_DATA_TYPE = "13";
    /**
     * 井盖数据包重复
     */
    public static final String MANHOLE_COVER_RETRY = "17";
    /**
     * 井盖电量
     */
    public static final String MANHOLE_COVER_ELECTRICITY = "18";
    /**
     * 井盖打开状态
     */
    public static final String MANHOLE_COVER_STATE = "19";
    /**
     * 集控器编号
     */
    public static final String GATEWAY_CODE = "3";
    /**
     * 集控器电压
     */
    public static final String GATEWAY_BATTERY = "9";

// =================================================井盖===================================================================



    /*=================================================航征begin===================================================================*/

    /**
     * 水位
     */
    public static final String WATER_RESOURCE_LEVEL = "1";

    /**
     * 瞬时流量
     */
    public static final String WATER_RESOURCE_FLUX = "2";
    /**
     * 累计流量
     */
    public static final String WATER_RESOURCE_FLUX_COUNT = "4";

    /**
     * 流速
     */
    public static final String WATER_RESOURCE_FLOW = "3";

    /*=================================================航征end===================================================================*/


// _________________________________________________燃气___________________________________________________________________

    /**
     * 燃气压强
     */
    public static final String GAS_PRE = "1";

    /**
     * 压强 kPa
     */
    public static final String GAS_KPA = "12";

    /**
     * 燃气硫化氢
     */
    public static final String GAS_H2S = "2";

    /**
     * 燃气甲烷
     */
    public static final String GAS_CH4 = "3";

    /**
     * 燃气温度
     */
    public static final String GAS_TEMPERATURE = "4";

    /**
     * 燃气井盖
     */
    public static final String GAS_MC = "5";

    /**
     * 燃气电压
     */
    public static final String GAS_VOLTAGE = "6";

    /**
     * 燃气通道号
     */
    public static final String GAS_LINE = "7";

    /**
     * 燃气检测值
     */
    public static final String GAS_CHECK = "8";

    /**
     * 燃气异常值
     */
    public static final String GAS_UNUSUAL = "9";

    /**
     * 燃气联网次数
     */
    public static final String GAS_CONN_NUM = "10";

    /**
     * 燃气网络信号值
     */
    public static final String GAS_CONN_SIGNAL = "11";


// _________________________________________________燃气___________________________________________________________________


// *************************************************桥梁*********************************************************************


    /**
     * 桥梁温度
     */
    public static final String BRIDGE_TEMPERATURE = "1";
    /**
     * 桥梁位移
     */
    public static final String BRIDGE_DISPLACEMENT = "2";

    /**
     * 桥梁位移2
     */
    public static final String BRIDGE_DISPLACEMENT_2 = "3";

    /**
     * 桥梁倾角
     */
    public static final String BRIDGE_DIP_ANGLE = "4";

    /**
     * 桥梁微应变
     */
    public static final String BRIDGE_MICRO_STRAIN = "5";

    /**
     * 桥梁液位变化
     */
    public static final String BRIDGE_LIQUID_LEVEL = "6";

    /**
     * 桥梁相对沉降量
     */
    public static final String BRIDGE_SETTLEMENT = "7";

// *************************************************桥梁*********************************************************************

//++++++++++++++++++++++++++++++++++++++++++++++++++泥沙流++++++++++++++++++++++++++++++++++++++++
    /**
     * 泥沙流泥沙含量
     */
    public static final String NSL_MUD_PERCENT = "1";

    /**
     * 泥沙流水位
     */
    public static final String NSL_WATERHIGH = "2";

    /**
     * 泥沙流温度
     */
    public static final String NSL_TEMPERATURE = "";

    /**
     * 泥沙流量
     */
    public static final String NSL_FLOW = "3";

    /**
     * 泥沙流读取设备地址
     */
    public static final String NSL_DEVICE_ADDR = "4";

    //------------------------------------------泥沙流报文类型-------------------------------------
    /**
     * 泥沙流报文类型--读取数据
     */
    public static final int NSL_READ_DATA = 17;

    /**
     * 泥沙流报文类型--读取地址
     */
    public static final int NSL_READ_ADDR = 7;

//++++++++++++++++++++++++++++++++++++++++++++++++++泥沙流++++++++++++++++++++++++++++++++++++++++


//-----------------------------------------------------供水，排水开始----------------------------------------------------
    /**
     * 表1净累计
     */
    public static final int PS_JING_LEI_JI = 1;

    /**
     * 表1正累计
     */
    public static final int PS_ZHENG_LEI_JI = 2;

    /**
     * 表1负累计
     */
    public static final int PS_FU_LEI_JI = 3;

    /**
     * 表1瞬时流量
     */
    public static final int PS_FLOW_RATE = 4;

    /**
     * 表2净累计
     */
    public static final int PS_BAO_LIU_5 = 5;
    /**
     * 表2正累计=（
     */
    public static final int PS_BAO_LIU_6 = 6;

    /**
     * 表2负累计
     */
    public static final int PS_ALARM = 7;

    /**
     * 旧（表2瞬时流量）
     * 2020 05 19 对于供水的流量计，故障代码为1600表示正常，16表示是否缩放，00表示正常，01表示仪表电池电压低于3.37V
     */
    public static final int PS_BAO_LIU_8 = 8;
    /**
     * 压力
     */
    public static final int PS_PRESSURE = 9;

    /**
     * 温度
     */
    public static final int PS_BAO_LIU_10 = 10;

    /**
     * 水位
     */
    public static final int PS_BAO_LIU_11 = 11;

    /**
     * 开关量
     */
    public static final int PS_ON_OFF = 12;
    public static final int PS_ON_OFF_1 = 13;
    public static final int PS_ON_OFF_2 = 14;
    public static final int PS_ON_OFF_3 = 15;
    public static final int PS_ON_OFF_4 = 16;
    public static final int PS_ON_OFF_5 = 17;
    public static final int PS_ON_OFF_6 = 18;
    public static final int PS_ON_OFF_7 = 19;
    public static final int PS_ON_OFF_8 = 20;
    public static final int PS_ON_OFF_9 = 21;
    public static final int PS_ON_OFF_10 = 22;
    public static final int PS_ON_OFF_11 = 23;
    public static final int PS_ON_OFF_12 = 24;
    public static final int PS_ON_OFF_13 = 25;
    public static final int PS_ON_OFF_14 = 26;
    public static final int PS_ON_OFF_15 = 27;
    public static final int PS_ON_OFF_16 = 28;
    public static final int PS_ON_OFF_17 = 29;
    public static final int PS_ON_OFF_18 = 30;
    public static final int PS_ON_OFF_19 = 31;
    public static final int PS_ON_OFF_20 = 32;
    public static final int PS_ON_OFF_21 = 33;
    public static final int PS_ON_OFF_22 = 34;
    public static final int PS_ON_OFF_23 = 35;
    public static final int PS_ON_OFF_24 = 36;
    public static final int PS_ON_OFF_25 = 37;
    public static final int PS_ON_OFF_26 = 38;
    public static final int PS_ON_OFF_27 = 39;
    public static final int PS_ON_OFF_28 = 40;
    public static final int PS_ON_OFF_29 = 41;
    public static final int PS_ON_OFF_30 = 42;
    public static final int PS_ON_OFF_31 = 43;

    /**
     * 频率
     */
    public static final int PS_HZ = 44;

    /**
     * 瞬时质量流量
     */
    public static final int PS_MASS_FLOW_RATE = 45;
    /**
     * 密度
     */
    public static final int PS_DENSITY = 46;
    /**
     * 电池电压
     */
    public static final int PS_BATTERY_VOLTAGE = 47;


    public static final int[] items = {PS_JING_LEI_JI, PS_ZHENG_LEI_JI, PS_FU_LEI_JI, PS_FLOW_RATE, PS_BAO_LIU_5,
            PS_BAO_LIU_6, PS_ALARM, PS_BAO_LIU_8, PS_PRESSURE, PS_BAO_LIU_10, PS_BAO_LIU_11,
            PS_ON_OFF, PS_ON_OFF_1, PS_ON_OFF_2, PS_ON_OFF_3, PS_ON_OFF_4, PS_ON_OFF_5, PS_ON_OFF_6,
            PS_ON_OFF_7, PS_ON_OFF_8, PS_ON_OFF_9, PS_ON_OFF_10, PS_ON_OFF_11, PS_ON_OFF_12,
            PS_ON_OFF_13, PS_ON_OFF_14, PS_ON_OFF_15, PS_ON_OFF_16, PS_ON_OFF_17, PS_ON_OFF_18,
            PS_ON_OFF_19, PS_ON_OFF_20, PS_ON_OFF_21, PS_ON_OFF_22, PS_ON_OFF_23, PS_ON_OFF_24,
            PS_ON_OFF_25, PS_ON_OFF_26, PS_ON_OFF_27, PS_ON_OFF_28, PS_ON_OFF_29, PS_ON_OFF_30,
            PS_ON_OFF_31, PS_HZ, PS_MASS_FLOW_RATE, PS_DENSITY};


    //-----------------------------------------------------供水，排水结束----------------------------------------------------
//---------------------------------------------------MQTT云飞虫情------------------------------------------------
//信号强度
    public static final int MQTT_SIGNAL = 1;
    //设备类型
    public static final int MQTT_DEVICE_TYPE = 2;
    //雨控状态 1 雨控，0 正常
    public static final int MQTT_RAIN_STATUS = 3;
    //光控状态 1 光控，0 正常
    public static final int MQTT_LIGHT_STATUS = 4;
    //温控状态 1 温控，0 正常
    public static final int MQTT_TEMPERATURE_STATUS = 5;
    //通道状态 1 落虫，0 排水
    public static final int MQTT_CHANNELSTATUS = 6;
    //上仓门状态  1 打开，0 关闭
    public static final int MQTT_UP_DOOR_STATUS = 7;
    //下仓门状态  1 打开，0 关闭
    public static final int MQTT_DOWN_DOOR_STATUS = 8;
    //加热状态  1 加热，0 关闭
    public static final int MQTT_HOT_STATUS = 9;
    //定时模式  0 光控 1 时控
    public static final int MQTT_TIMING_STATUS = 10;
    //纬度
    public static final int MQTT_LAT = 11;
    //经度
    public static final int MQTT_LNG = 12;

    //---------------------------------------------------MQTT云飞虫情------------------------------------------------
//---------------------------------------------------水质开始------------------------------------------------
    //仪器门状态
    public static final String WATER_SAMPLE_INSTRUMENT_DOOR = "1";

    //仪器前一次采样的进水状态
    public static final String WATER_SAMPLE_FIRST_STATUS = "2";
    //排放口液位信号
    public static final String WATER_SAMPLE_LIQUID_LEVEL_SIGNAL = "3";
    //仪器电源状态
    public static final String WATER_SAMPLE_POWER_STAUS = "4";
    //仪器是否启动
    public static final String WATER_SAMPLE_STATUS = "5";
    //水箱温度
//    public static final int  WATER_CISTERN_TEM = 6;


    public static final String WATER_SAMPLE_UV = "6";
    public static final String WATER_SAMPLE_SS = "7";
    public static final String WATER_SAMPLE_NH3 = "8";
    public static final String WATER_SAMPLE_PH = "9";
    public static final String WATER_SAMPLE_TEM = "10";

    //瞬时流量
    public static final String WATER_QUALITY_FLOW_RATE = "11";
    //总磷值
    public static final String WATER_QUALITY_P = "12";
    //累积流量
    public static final String WATER_QUALITY_TOTAL_FLOW_RATE = "13";


//---------------------------------------------------水质结束------------------------------------------------
}
