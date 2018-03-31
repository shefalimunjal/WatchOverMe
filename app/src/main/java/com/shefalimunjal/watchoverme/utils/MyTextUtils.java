package com.shefalimunjal.watchoverme.utils;

import android.text.TextUtils;

public class MyTextUtils {
    private MyTextUtils(){

    }

    public static boolean isValidPhnNo(String phnNo){
        return !TextUtils.isEmpty(phnNo)
                && phnNo.length() == 10
                && phnNo.matches("[0-9]+");
    }
}