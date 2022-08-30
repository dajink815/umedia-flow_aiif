import com.uangel.aiif.util.DateFormatUtil;
import com.uangel.aiif.util.FileUtil;
import org.junit.Test;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Objects;

/**
 * @author dajin kim
 */
public class FileTest {

    @Test
    public void fileCreationTime() {
        String filePath = "/Users/kimdajin/UMediaFlow/umedia-flow_aiif/src/test/resources/tts/cache/greeting.wav";
        FileTime fileTime = FileUtil.getLastModifiedTime(filePath);
        System.out.println(fileTime);
        System.out.println(DateFormatUtil.formatYmdHmsS(fileTime.toMillis()));

        long gap = System.currentTimeMillis() - fileTime.toMillis();
        System.out.println("Mins : " + gap/1000/60);
        System.out.println("Hours : " + gap/1000/60/60);
        FileUtil.printGapTime(filePath);
    }

    @Test
    public void dirFileList() {
        String dirPath = "/Users/kimdajin/UMediaFlow/umedia-flow_aiif/src/main/resources/tts/";
        List<File> fileList = FileUtil.getDirFileList(dirPath);

        // 24 시간 millis  /min /sec /millis
        long timer = 140 * 60 * 60 * 1000;

        fileList.stream()
                .filter(Objects::nonNull)
                .filter(File::isFile)
                .filter(file -> !file.getName().startsWith("."))
                // Check LongFile
                .filter(file -> checkTimeout(file.getAbsolutePath(), timer))
                .forEach(file -> {
                    System.out.println(file.getName());

                    FileTime fileTime = FileUtil.getLastModifiedTime(file.getAbsolutePath());
                    System.out.println(DateFormatUtil.formatYmdHmsS(fileTime.toMillis()));

                    FileUtil.printGapTime(file.getAbsolutePath());

                    // 오래된 파일 제거
                });
    }

    private boolean checkTimeout(String filePath, long timer) {
        if (timer <= 0) return false;
        long modifiedTime = FileUtil.getLastModifiedTime(filePath).toMillis();
        return modifiedTime > 0 && modifiedTime + timer < System.currentTimeMillis();
    }
}
