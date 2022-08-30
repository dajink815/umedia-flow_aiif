package com.uangel.aiif.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author dajin kim
 */
public class FileUtil {
    static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
        // nothing
    }

    public static boolean isExist(String filePathName) {
        File file = new File(filePathName);
        return file.exists();
    }

    public static boolean isDir(String dirPath) {
        File dir = new File(dirPath);
        return dir.isDirectory();
    }

    public static boolean isFile(String filePathName) {
        File file = new File(filePathName);
        return file.isFile();
    }

    public static String getFileName(String filePath) {
        return new File(filePath).getName();
    }

    public static void createDir(String dirPath) {
        createDir(new File(dirPath));
    }

    public static void createDir(File newDir) {
        if (!newDir.exists()) {
            try {
                if (newDir.mkdir()) {
                    log.debug("Created New Directory [{}]", newDir.getName());
                }
            } catch (Exception e) {
                log.error("FileUtil.createDir.Exception [{}] ", newDir.getName(), e);
            }
        }
    }

    public static File createFile(String dirPath, String fileName) {
        return createFile(new File(dirPath, fileName));
    }

    public static File createFile(File newFile) {
        if (!newFile.exists()) {
            try {
                if (newFile.createNewFile()) {
                    log.debug("Created New File [{}]", newFile.getName());
                }
            } catch (Exception e) {
                log.error("FileUtil.createFile.Exception [{}] ", newFile.getName(), e);
            }
        }

        return newFile;
    }

    public static void deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            if (Files.deleteIfExists(path)) {
                log.debug("Deleted File [{}]", path.getFileName());
            }
        } catch (Exception e) {
            log.error("FileUtil.deleteFile.Exception [{}] ", path.getFileName(), e);
        }
    }

    public static void writeFile(String fileName, String data, boolean append) {
        File file = new File(fileName);
        try(FileWriter fw = new FileWriter(file, append)) {
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(data);
            writer.close();
        } catch (Exception e) {
            log.error("FileUtil.writeFile.Exception ", e);
        }
    }

    public static String readFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
                sb.append("\r\n");
            }
        } catch (Exception e) {
            log.error("FileUtil.readFile.Exception", e);
        }
        return sb.toString();
    }

    public static void byteArrayToFile(byte[] byteArray, String filePath){
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(byteArray);
            log.debug("FileUtil Byte --> File [{}]", filePath);
        } catch (IOException e) {
            log.error("FileUtil.byteArrayToFile.Exception ", e);
        }
    }

    public static byte[] fileToByteArray(String fileName) {
        byte[] result = new byte[0];
        try {
            result = inputStreamToByteArray(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            log.error("FileUtil.fileToByteArray.Exception ", e);
        }
        return result;
    }

    // 테스트 필요
    public static byte[] inputStreamToByteArray(InputStream inStream){
        InputStreamReader in = new InputStreamReader(inStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] result = new byte[0];
        try {
            int next = inStream.read();
            while (next > -1){
                baos.write(next);
                next = in.read();
            }

            result = baos.toByteArray();
            baos.flush();
            in.close();
        } catch (IOException e) {
            log.error("FileUtil.inputStreamToByteArray.Exception ", e);
        }
        return result;
    }

    public static FileTime getCreationTime(String filePath) {
        return getFileTime(filePath, "creationTime");
    }

    public static FileTime getLastModifiedTime(String filePath) {
        return getFileTime(filePath, "lastModifiedTime");
    }

    public static FileTime getFileTime(String filePath, String timeAttr) {
        FileTime fileTime = null;
        Path path = Paths.get(filePath);

        try {
            fileTime = (FileTime) Files.getAttribute(path, timeAttr);
        } catch (IOException e) {
            log.error("FileUtil.getFileTime.Exception (Attribute: {})", timeAttr, e);
        }
        return fileTime;
    }

    public static void printGapTime(String filePath) {
        FileTime fileTime = getLastModifiedTime(filePath);
        if (fileTime == null) return;
        String modifiedTime = DateFormatUtil.formatYmdHmsS(fileTime.toMillis());
        log.debug("File:[{}], LastModifiedTime:[{}]", getFileName(filePath), modifiedTime);

        long gap = System.currentTimeMillis() - fileTime.toMillis();
        int gapDays = (int) (gap/1000/60/60/24);
        int gapHours = (int) ((gap/1000/60/60)%24);
        int gapMins = (int) ((gap/1000/60)%60);
        log.debug("GAP [{} Days, {} Hours, {} Mins]", gapDays, gapHours, gapMins);
    }

    public static List<File> getDirFileList(String dirPath) {
        // check dir
        if (!isDir(dirPath)) return Collections.emptyList();
        File[] files = new File(dirPath).listFiles();
        if (files == null || files.length == 0) return Collections.emptyList();
        return Arrays.asList(files);
    }
}
