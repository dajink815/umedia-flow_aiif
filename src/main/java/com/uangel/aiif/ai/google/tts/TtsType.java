package com.uangel.aiif.ai.google.tts;

/**
 * @author dajin kim
 */
public enum TtsType {
    FILE(0),
    MENT(1);

    private final int value;

    TtsType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TtsType getTypeEnum(int type) {
        switch (type) {
            case 0: return FILE;
            case 1:
            default:
                return MENT;
        }
    }
}
