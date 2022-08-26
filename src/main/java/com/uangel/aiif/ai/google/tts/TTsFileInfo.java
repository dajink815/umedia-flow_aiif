package com.uangel.aiif.ai.google.tts;

/**
 * @author dajin kim
 */
public class TTsFileInfo {

    private final int hashCode;
    private final String fileName;
    private final long createTime;

    public TTsFileInfo(int hashCode, String fileName) {
        this.hashCode = hashCode;
        this.fileName = fileName;
        this.createTime = System.currentTimeMillis();
    }

    public int getHashCode() {
        return hashCode;
    }

    public String getFileName() {
        return fileName;
    }

    public long getCreateTime() {
        return createTime;
    }
}
