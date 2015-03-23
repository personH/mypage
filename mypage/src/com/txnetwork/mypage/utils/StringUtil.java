package com.txnetwork.mypage.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import com.txnetwork.mypage.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return true 不为空，false 为空
     */
    public static boolean isNotNull(String str) {
        if (str != null && !TextUtils.isEmpty(str) && !"".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为null或空值
     *
     * @param str String
     * @return true or false
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     */
    public static String toString(String str) {
        if (str != null && !TextUtils.isEmpty(str) && !"".equals(str.trim())) {
            return str;
        }
        return "";
    }

    public static boolean isRightPhone(String phoneStr) {

        // String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";//^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$
        Pattern p = Pattern.compile("^(13|15|14|17|18)\\d{9}$");
        Matcher m = p.matcher(phoneStr);
        if (!m.matches()) {
            // 请输入正确手机号码
            return false;
        }
        return true;
    }

}
