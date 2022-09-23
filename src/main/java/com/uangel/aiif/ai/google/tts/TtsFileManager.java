package com.uangel.aiif.ai.google.tts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dajin kim
 */
public class TtsFileManager {
    static final Logger log = LoggerFactory.getLogger(TtsFileManager.class);
    private static TtsFileManager ttsFileManager = null;
    private static final ConcurrentHashMap<Integer, TTsFileInfo> ttsFileMap = new ConcurrentHashMap<>();

    private TtsFileManager() {
        // nothing
    }

    public static TtsFileManager getInstance() {
        if (ttsFileManager == null)
            ttsFileManager = new TtsFileManager();
        return ttsFileManager;
    }

    public void addTtsFile(String content, String fileName) {
        this.addTtsFile(content.hashCode(), fileName);
    }
    public void addTtsFile(int key, String fileName) {
        if (ttsFileMap.containsKey(key)) {
            log.error("TTS File [{}] Exist", fileName);
            return;
        }

        TTsFileInfo fileInfo = new TTsFileInfo(key, fileName);
        ttsFileMap.put(key, fileInfo);
        log.warn("TTS File [{}] Added", fileName);
    }

    public String getTtsFileName(String content) {
        return this.getTtsFileName(content.hashCode());
    }
    public String getTtsFileName(int key) {
        TTsFileInfo fileInfo = ttsFileMap.get(key);
        if (fileInfo == null) {
            log.warn("TTS File Null (key:{})", key);
            return null;
        }

        return fileInfo.getFileName();
    }

    public void deleteTtsFile(String content) {
        this.deleteTtsFile(content.hashCode());
    }
    public void deleteTtsFile(int key) {
        TTsFileInfo fileInfo = ttsFileMap.remove(key);
        if (fileInfo != null) {
            log.warn("TTS File [{}] Removed", fileInfo.getFileName());
        }
    }
}
