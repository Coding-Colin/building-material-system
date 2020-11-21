package com.contract.system.util;

import java.util.ArrayList;
import java.util.List;

public class SelectUtil {

    public static List<Integer> getSelectId(String s){
        List<Integer> list = new ArrayList<Integer>();
        String str[] = s.split(",");
        for (int i=0;i<s.length()-1;i++){
            list.add(Integer.parseInt(str[i]));
        }
        return list;
    }
}
