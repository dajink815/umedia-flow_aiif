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
    private static final ConcurrentHashMap<Integer, String> ttsFileMap = new ConcurrentHashMap<>();

    private TtsFileManager() {
        // nothing
    }

    public static TtsFileManager getInstance() {
        if (ttsFileManager == null)
            ttsFileManager = new TtsFileManager();
        return ttsFileManager;
    }

    public void addTtsFile(int key, String fileName) {
        if (ttsFileMap.containsKey(key)) {
            log.error("TTS File [{}] Exist", fileName);
            return;
        }

        ttsFileMap.put(key, fileName);
        log.warn("TTS File [{}] Added", fileName);
    }

    public String getTtsFileName(int key) {
        String fileName = ttsFileMap.get(key);
        if (fileName == null) {
            log.warn("TTS File Null (key:{})", key);
        }
        return fileName;
    }

    public void deleteTtsFile(int key) {
        String fileName = ttsFileMap.remove(key);
        if (fileName != null) {
            log.warn("TTS File [{}] Removed", fileName);
        }
    }
}
