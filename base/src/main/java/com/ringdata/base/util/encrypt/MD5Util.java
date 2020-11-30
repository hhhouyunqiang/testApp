package com.ringdata.base.util.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by admin on 17/11/6.
 */

public class MD5Util {
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }
}
