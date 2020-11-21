package com.contract.system.util;

import java.util.UUID;

public class UUIDUtil {

    /**
     * 生成合同号
     * @return
     */
    public static String makeUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }
}
