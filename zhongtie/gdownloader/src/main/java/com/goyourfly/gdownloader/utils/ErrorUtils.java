package com.goyourfly.gdownloader.utils;

/**
 * Created by gaoyufei on 15/12/29.
 */
public class ErrorUtils {
    public static String getError(String err) {
        Ln.e("GetError:" + err);
        if (err == null)
            return "";
        if (err.contains("UnknownHostException")) {
            return " 连接异常 ";
        }
        if (err.contains("java.net.UnknownHostException")) {
            return " 连接异常 ";
        }

        if (err.contains("NetworkErrorException")) {
            return err.substring(err.indexOf("NetworkErrorException") + "NetworkErrorException".length());
        }
        return err;
    }

    public static String getError(Throwable err) {
        return getError(err.getMessage());
    }
}
