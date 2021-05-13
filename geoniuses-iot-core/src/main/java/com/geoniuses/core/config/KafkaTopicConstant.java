package com.geoniuses.core.config;

/**
 * @auther: zyf
 * @Date: 2020/09/16 15:55
 * @Description
 */
public class KafkaTopicConstant {


    public static final String TEMPERATURE_TOPIC = "ZYDL_WellCover99_Airport_Temperature";
    public static final String OPEN_TOPIC = "ZYDL_WellCover99_Airport_Open";
    public static final String HARM_GAS_TOPIC = "ZYDL_WellCover99_Airport_Harm_Gas";
    public static final String LIQUID_LEVEL_TOPIC = "ZYDL_WellCover99_Airport_Liquid_Level";

    public static final String TEMPERATURE_PUSH_TOPIC = "ZYDL_WellCover99_Push_Airport_Temperature";
    public static final String OPEN_PUSH_TOPIC = "ZYDL_WellCover99_Push_Airport_Open";
    public static final String HARM_GAS_PUSH_TOPIC = "ZYDL_WellCover99_Push_Airport_Harm_Gas";
    public static final String LIQUID_LEVEL_PUSH_TOPIC = "ZYDL_WellCover99_Push_Airport_Liquid_Level";

    public KafkaTopicConstant() {
        throw new IllegalStateException("Utility class");
    }
}
