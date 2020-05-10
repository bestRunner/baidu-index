package com.baidu.index.utils;

public class LogbackUtil {

    /**
     * 对异常信息进行转换
     */
    public static String expection2Str(Exception e) {
        String result = e.getMessage() + ";";
        for (StackTraceElement stack : e.getStackTrace()) {
            result += stack.toString() + ";";
        }

        return result.substring(0, result.length() - 1).replaceAll("\n", ",");
    }
}
