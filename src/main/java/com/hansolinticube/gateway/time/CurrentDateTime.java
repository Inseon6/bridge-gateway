package com.hansolinticube.gateway.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentDateTime {
    public static String getCurrentDateTime() {
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(nowDate);
    }
}
