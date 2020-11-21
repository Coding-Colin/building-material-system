package com.contract.system.util;
import com.contract.system.bean.Contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {


    public static final Long ONEDAY = 3600 * 60 * 24L;

    public static List<String> dateCompare(List<Contract> list){
        long nowcurr = new Date().getTime();
        List<String> result = new ArrayList<String>();
        for (int i=0;i<list.size();i++){
            long curr = list.get(i).endDate.getTime();
            if (nowcurr-curr<ONEDAY){
                result.add(list.get(i).title+"-合同不足一天");
            }
            else if(nowcurr-curr<ONEDAY*3){
                result.add(list.get(i).title+"-合同不足三天");
            }
            else if(nowcurr-curr<ONEDAY*7){
                result.add(list.get(i).title+"-合同不足七天");
            }
        }
        return result;
    }

    public static Date parse(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 =  sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date2;
    }
}
