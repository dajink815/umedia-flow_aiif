package com.uangel.aiif.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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
}
