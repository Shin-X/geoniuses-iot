package com.geoniuses.core.enums;




public enum ProtocolEnum implements Transport {
    HTTP("HTTP"),
    MQTT("MQTT"),
    MODBUS("MODBUS"),
    //交通部标准
    JT808("JT808"),
    //交通部标准
    JT809("JT809"),
    COAP("COAP"),
    UDP("UDP"),
    NSL("NSL"),
    //气象
    WEATHER("WEATHER"),
    //燃气
    GAS("GAS"),
    //排水
    PS("PS"),
    //水质
    WATER("WATER"),
    //mqtt客户端
    MQTTCLI("MQTTCLI"),
    //公交客户端
    BUSCLI("BUSCLI"),
    KAFKA("KAFKA"),
    //污水
    SEWAGE("SEWAGE"),
    //FW4100超声波液位仪
    FW4100("FW4100"),
    //水质标准协议
    WATERRESOURCE("WATERRESOURCE"),
    //websocket
    WEBSOCKET("WEBSOCKET"),
    //桥梁
    BRIDGECLI("BRIDGECLI");

    private final String value;

    ProtocolEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ProtocolEnum getProtocol(String type) {
        for (ProtocolEnum t : values()) {
            if (t.value.equals(type)) {
                return t;
            }
        }
//        throw new IllegalArgumentException("unknown message type: " + type);
        return null;
    }
}
