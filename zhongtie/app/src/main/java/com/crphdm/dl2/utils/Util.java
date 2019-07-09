package com.crphdm.dl2.utils;

import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Modify by songzixuan on 19/07/04.
 */
public class Util {
    public static int getBookIdByUrl(String url){
        Uri myUri = Uri.parse(url);
        int bookId = 0;

        if (myUri.getQueryParameter("bookid") != null) {
            bookId = Integer.valueOf(myUri.getQueryParameter("bookid"));
        }

        return bookId;
    }

    /**
     * 判断密码是否为纯数字
     * @return
     */
    public static boolean isPureDigitalPassword(String password){
        String strPattern = "^\\d\\d*$";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * 验证邮箱输入是否合法
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
// String strPattern =
// "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 验证是否是手机号码
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
