package com.uangel.aiif.service.schedule.handler;

import com.uangel.aiif.rmq.handler.aiwf.incoming.RmqTtsStartReq;
import com.uangel.aiif.service.schedule.base.IntervalTaskUnit;
import com.uangel.aiif.util.DateFormatUtil;
import com.uangel.aiif.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author dajin kim
 */
public class LongFileHandler extends IntervalTaskUnit {
    private static final Logger log = LoggerFactory.getLogger(LongFileHandler.class);

    // todo 주기
    public LongFileHandler(int interval) {
        super(interval);
    }

    @Override
    public void run() {
        checkLongFile();
    }

    private void checkLongFile() {
        // Long File Timer (Unit Hours -> Millis)
        long timer = (long) config.getLongFile() * 60 * 60 * 1000;

        // Cache dir 경로 (FileBase + /cache/)
        String dirPath = config.getMediaFilePath() + RmqTtsStartReq.CACHE_DIR;

        // Cache dir 파일 리스트 조회
        List<File> fileList = FileUtil.getDirFileList(dirPath);
        List<String> nameList = new ArrayList<>();
        for (File file : fileList) {
            nameList.add(file.getName());
        }
        log.debug("LongFileHandler - FileList({}): {}", nameList.size(), nameList);

        fileList.stream()
                .filter(Objects::nonNull)
                .filter(File::isFile)
                // 숨김 파일 제외
                .filter(file -> !file.getName().startsWith("."))
                // 오래된 파일 체크
                .filter(file -> checkTimeout(file.getAbsolutePath(), timer))
                .forEach(file -> {
                    FileTime fileTime = FileUtil.getLastModifiedTime(file.getAbsolutePath());
                    String lastModified =  DateFormatUtil.formatYmdHmsS(fileTime.toMillis());
                    int gapHour = (int) (System.currentTimeMillis() - fileTime.toMillis())/1000/60/60;
                    log.warn("File TIMEOUT [F:{}] [T:{}h] [G:{}h] [L:{}]", file.getName(), config.getLongFile(), gapHour, lastModified);

                    // 파일 제거
                    //FileUtil.deleteFile(file.getAbsolutePath());
                });
    }

    private boolean checkTimeout(String filePath, long timer) {
        if (timer <= 0) return false;
        long modifiedTime = FileUtil.getLastModifiedTime(filePath).toMillis();
        return modifiedTime > 0 && modifiedTime + timer < System.currentTimeMillis();
    }
}
