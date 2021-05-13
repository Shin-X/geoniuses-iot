package com.geoniuses.core.pojo;

public enum LiquidLevelEnum {
    WATERSTAUS("waterstaus", "10"),
    BATTERYVOLTAGE("voltage", "2"),
    WATERLEVEL("waterlevel", "11"),
    SIGN("sign", "1"),
    STATUS("status", "4");

    private String key;
    private String value;

    LiquidLevelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(String key) {
        for (LiquidLevelEnum se : values()) {
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
