package com.arki.backuphelper.base.utils;

public class CommonUtil {
    public static String encodeHex(byte[] b, boolean lowerCase) {
        if (b == null || b.length == 0) {
            return "";
        }
        char[] digestChar = lowerCase
                ? new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}
                : new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(digestChar[b[i] >>> 4 & 0xf]);
            sb.append(digestChar[b[i] & 0xf]);
        }
        return sb.toString();
    }
}
