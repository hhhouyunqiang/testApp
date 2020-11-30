package com.ringdata.base.util;

public class GUIDUtil {

    public static String getGuidStr() {
        return deleteAsscii(java.util.UUID.randomUUID().toString());
    }

    private static String deleteAsscii(String str) {
        String upStr = str.toUpperCase();
        String lowStr = str.toLowerCase();
        StringBuffer buf = new StringBuffer(str.length());

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == upStr.charAt(i)) {
                buf.append(lowStr.charAt(i));
            } else {
                buf.append(upStr.charAt(i));
            }
        }
        return buf.toString();
    }

}
