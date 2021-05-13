package com.geoniuses.core.pojo;

public enum HarmGasEnum {

    H2S("H2S", "12"),
    O2("O2", "13"),
    CO("CO", "14"),
    CH4("CH4", "15"),
    TEM("temperature", "16"),
    BATTERYVOLTAGE("battery", "2"),
    SIGN("sign", "1"),
    LIQUIDLEVEL("liquidlevel", "17"),
    ON_OFF("slope", "4");

    private String key;
    private String value;

    HarmGasEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(String key) {
        for (HarmGasEnum se : values()) {
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
