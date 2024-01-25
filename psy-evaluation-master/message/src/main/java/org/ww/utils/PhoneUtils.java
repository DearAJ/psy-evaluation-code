package org.ww.utils;

import org.apache.commons.lang.StringUtils;

public class PhoneUtils {
    public static String toFullPhoneNumber(String phone)
    {
        if(StringUtils.isBlank(phone) || phone.startsWith("00") || phone.startsWith("+"))
        {
            return phone;
        }

        return "0086" + phone;
    }
}
