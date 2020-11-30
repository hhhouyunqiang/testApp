package com.ringdata.base;

import android.support.test.runner.AndroidJUnit4;

import com.ringdata.base.util.encrypt.MD5Util;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.

            final String key = "ringsurvey201806v3";
            final long time = new Date().getTime();
            System.out.print(time);
            final String code = MD5Util.md5(MD5Util.md5(time + key));
            System.out.print(code);

//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        assertEquals("com.ringdata.base.test", appContext.getPackageName());
    }
}
