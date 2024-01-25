package org.ww.utils;

import java.util.Calendar;

public class SchoolYearUtils {

    private static final int YEAR_SEP= 701; // 代表：8月1日
    public static int currentSchoolYear()
    {
        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.MONTH) * 100 + now.get(Calendar.DAY_OF_MONTH) >= YEAR_SEP)
        {
            return now.get(Calendar.YEAR);
        }

        return now.get(Calendar.YEAR) - 1;
    }

    public static int lastSchoolYear()
    {
        return currentSchoolYear() - 1;
    }

}
