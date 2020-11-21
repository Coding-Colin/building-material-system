package com.contract.system.util;

import java.io.UnsupportedEncodingException;


/**
 * 解决@RequestParam乱码问题
 */
public class EncodingTool {

    public static String encodeStr(String str){
        try {
            return new String(str.getBytes("ISO-8859-1"),"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

}
