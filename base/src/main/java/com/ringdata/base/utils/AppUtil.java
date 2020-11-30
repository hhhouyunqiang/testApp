package com.ringdata.base.utils;

import java.io.File;

/**
 * Created by admin on 2017/12/29.
 */

public class AppUtil {
    public static boolean isPackageInstalled(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
