package com.contract.system.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
     */
    public static void downfile(HttpServletResponse response ,String path) throws IOException {
        String suffix = path.split("\\.")[path.split("\\.").length - 1];
        response.setHeader("content-disposition","attachment;fileName="+"contract."+suffix);
        InputStream in = new FileInputStream(path); //获取下载文件的输入流
        int count =0;
        byte[] by = new byte[1024];
        OutputStream out=  response.getOutputStream();
        while((count=in.read(by))!=-1){
            out.write(by, 0, count);//将缓冲区的数据输出到浏览器
        }
        in.close();
        out.flush();
        out.close();
    }
}

