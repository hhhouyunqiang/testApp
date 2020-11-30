package com.ringdata.base.util.dimen;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.ringdata.base.app.Latte;


/**
 * Created by admin on 17/10/12.
 */

public final class DimenUtil {

    public static int getScreenWidth() {
        final Resources resources = Latte.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        final Resources resources = Latte.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
