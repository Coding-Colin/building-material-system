package com.contract.system.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateUtil {


    public static Date StringToDate(String date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
            Date result = simpleDateFormat.parse(date);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
