package com.geoniuses.core.pojo;

public enum TemperatureEnum {

    STATUS("status", "4"),
    VOLTAGE("voltage", "2"),
    SIGN("sign", "1"),
    TEMPERATURE("temperature", "8"),
    HUMIDITY("humidity", "9");

    private String key;
    private String value;

    TemperatureEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(String key) {
        for (TemperatureEnum se : values()) {
            if (se.getKey().equalsIgnoreCase(key)) {
                return se.getValue();
            }
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
