package com.uangel.aiif.service;

/**
 * @author dajin kim
 */
public enum ServiceDefine {
    LANG_CODE("ko-KR"), MEDIA_FILE_EXTENSION(".wav");

    private String str;
    private int num;

    ServiceDefine(String str) {
        this.str = str;
    }

    ServiceDefine(int num) {
        this.num = num;
    }

    public String getStr() {
        return str;
    }

    public int getNum() {
        return num;
    }
}
