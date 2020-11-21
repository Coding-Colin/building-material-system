package com.contract.system.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class FileUtil {


    public static final String PATH = "F:\\";

    /**
     * 删除文件方法
     *
     * @param path
     */
    public static void delete(String path) {
        File file = new File(path);
        if (file != null) {
            file.delete();
        }
    }

    /**
     * 下载文件
     *
     * @param
     */
    public static void copy(String filename, String oldpath, String newpath) throws IOException {
        File oldpaths = new File(oldpath);
        File newpaths = new File(newpath + "/" + filename);
        if (!newpaths.exists()) {
            Files.copy(oldpaths.toPath(), newpaths.toPath());
        } else {
            newpaths.delete();
            Files.copy(oldpaths.toPath(), newpaths.toPath());
        }

    }
}

