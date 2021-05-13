package com.geoniuses.core.pojo;

/**
 * @auther zyf
 * @Date: 2020/9/10 15:01
 * @Description
 */
public class MqttTopics {
    
    public static final String BASE_TOPIC = "/zydl";

    //发布端
    public static final String PUB_TOPIC = "/pub";

    //订阅端
    public static final String SUB_TOPIC = "/sub";

    //井盖
    public static final String MANHOLE_COVER_TOPIC = "/wellcover99";

    //井盖温度
    public static final String TEMPERATURE_TOPIC = BASE_TOPIC + MANHOLE_COVER_TOPIC +"/wd" + PUB_TOPIC;
    //液位水压
    public static final String YW_TOPIC = BASE_TOPIC + MANHOLE_COVER_TOPIC +"/yw"+ PUB_TOPIC;
    //井盖离位
    public static final String OPEN_TOPIC = BASE_TOPIC + MANHOLE_COVER_TOPIC +"/lw"+ PUB_TOPIC;
    //有害气体
    public static final String GAS_TOPIC = BASE_TOPIC + MANHOLE_COVER_TOPIC +"/qt"+ PUB_TOPIC;



//    public static final String yz = BASE_TOPIC + "/zydl/wellcover99/qt/pub";



}
