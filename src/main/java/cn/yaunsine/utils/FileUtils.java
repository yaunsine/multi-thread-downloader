package cn.yaunsine.utils;

import java.io.File;

/**
 * 文件工具类
 */
public class FileUtils {
    /**
     * 获取本地文件的大小
     * @param path 本地路径
     * @return
     */
    public static long getFileContentLength(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() ? file.length() : 0;
    }
}
