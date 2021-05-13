package com.geoniuses.core.pojo.manholecover;


import java.util.Map;
import java.util.Objects;

/**
 * @Author zyf
 * @Date 2019/10/25 11:42
 */
public class ManholeCoverMessage {

    //数据buf
    private Object hexData;

    //解析范式
    private Map<String,Object> parseJson;

    //发送到kafka的主题
    private String topic;

    public Object getHexData() {
        return hexData;
    }

    public void setHexData(Object hexData) {
        this.hexData = hexData;
    }

    public Map<String, Object> getParseJson() {
        return parseJson;
    }

    public void setParseJson(Map<String, Object> parseJson) {
        this.parseJson = parseJson;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManholeCoverMessage that = (ManholeCoverMessage) o;
        return Objects.equals(hexData, that.hexData) &&
                Objects.equals(parseJson, that.parseJson) &&
                Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hexData, parseJson, topic);
    }

    @Override
    public String toString() {
        return "ModbusMessage{" +
                "hexData=" + hexData +
                ", parseJson=" + parseJson +
                ", topic='" + topic + '\'' +
                '}';
    }
}
