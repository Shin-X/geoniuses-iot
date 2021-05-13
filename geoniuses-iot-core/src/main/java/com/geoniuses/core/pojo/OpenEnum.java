package com.geoniuses.core.pojo;

/**
 * 井盖离位枚举类
 */
public enum OpenEnum {

    SIGN("sign", "1"),
    BATTERYVOLTAGE("voltage", "2"),
    WATEROUT("waterOut", "3"),
    STATUS("status", "4"),
    XPITCH("xPitch", "5"),
    ZROLL("zRoll", "6"),
    LEVELANGLE("levelAngle", "7");

    private String key;
    private String value;

    OpenEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(String key) {
        for (OpenEnum se : values()) {
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
