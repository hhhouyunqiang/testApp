package com.ringdata.ringinterview.capi.components.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.app.ActivityCompat;

import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 2017/12/29.
 */

public class AppUtil {
    public static String WECHAT_APP_ID = "wx09658b7d50b0fcee";

    public static boolean isPackageInstalled(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    public static String getSurveyStatus(int status) {
        String statusStr = "";
        switch (status) {
            case 0:
                statusStr = "准备中";
                break;
            case 1:
                statusStr = "已启动";
                break;
            case 2:
                statusStr = "已暂停";
                break;
            case 3:
                statusStr = "已结束";
                break;
        }
        return statusStr;
    }

    public static int getSurveyStatusColor(int status) {
        int color = R.color.color08;
        switch (status) {
            case 0:
                color = R.color.item_preparing;
                break;
            case 1:
                color = R.color.item_started;
                break;
            case 2:
                color = R.color.item_stopped;
                break;
            case 3:
                color = R.color.item_finished;
                break;
        }
        return color;
    }

    public static String getSurveyTime(int status, String createTime, String updateTime, String beginTime, String pauseTime, String endTime) {
        String timeStr = "";
        switch (status) {
            case 0:
                if (createTime != null) {
                    timeStr = "创建：" + createTime;
                } else {
                    timeStr = "更新：" + updateTime;
                }
                break;
            case 1:
                timeStr = "启动：" + beginTime;
                break;
            case 2:
                timeStr = "结束：" + pauseTime;
                break;
            case 3:
                timeStr = "结束：" + endTime;
                break;
        }
        return timeStr;
    }

    public static String getSurveyTypeIcon(String type) {
        String type_icon = "";
        switch (type) {
            case ProjectConstants.CADI_PROJECT:
                type_icon = "{icon-cadi}";
                break;
            case ProjectConstants.CAPI_PROJECT:
                type_icon = "{icon-capi}";
                break;
            case ProjectConstants.CATI_PROJECT:
                type_icon = "{icon-cati}";
                break;
            case ProjectConstants.CAWI_PROJECT:
                type_icon = "{icon-cawi}";
                break;
            case ProjectConstants.CAXI_PROJECT:
                type_icon = "{icon-caxi}";
                break;
        }
        return type_icon;
    }

    public static String getSurveyTypeText(String type) {
        String type_text = "";
        switch (type) {
            case ProjectConstants.CADI_PROJECT:
                type_text = "录入";
                break;
            case ProjectConstants.CAPI_PROJECT:
                type_text = "面访";
                break;
            case ProjectConstants.CATI_PROJECT:
                type_text = "电话";
                break;
            case ProjectConstants.CAWI_PROJECT:
                type_text = "网络";
                break;
            case ProjectConstants.CAXI_PROJECT:
                type_text = "混合";
                break;
        }
        return type_text;
    }

    public static String getSampleProperty(String code) {
        String property_text = "";
        switch (code) {
            case "name":
                property_text = "样本名称";
                break;
            case "code":
                property_text = "样本编号";
                break;
            case "gender":
                property_text = "性别";
                break;
            case "organization":
                property_text = "单位";
                break;
            case "birth":
                property_text = "出生日期";
                break;
            case "age":
                property_text = "年龄";
                break;
            case "mobile":
                property_text = "电话";
                break;
            case "weixin":
                property_text = "微信";
                break;
            case "phone":
                property_text = "手机";
                break;
            case "qq":
                property_text = "qq";
                break;
            case "email":
                property_text = "邮箱";
                break;
            case "weibo":
                property_text = "微博";
                break;
            case "province":
                property_text = "省";
                break;
            case "city":
                property_text = "市";
                break;
            case "district":
                property_text = "县";
                break;
            case "town":
                property_text = "街道/镇";
                break;
            case "address":
                property_text = "详细地址";
                break;
            case "marriageStatus":
                property_text = "婚姻状况";
                break;
            case "education":
                property_text = "学历";
                break;
            case "politicalStatus":
                property_text = "政治面貌";
                break;
            case "nationality":
                property_text = "国籍";
                break;
            case "profession":
                property_text = "职业";
                break;
            case "position":
                property_text = "职务";
                break;
            case "placeOfBirth":
                property_text = "籍贯";
                break;
            case "religion":
                property_text = "宗教信仰";
                break;
            case "language":
                property_text = "语言";
                break;
            case "dialects":
                property_text = "方言";
                break;
            case "description":
                property_text = "备注";
                break;
            case "detail":
                property_text = "详细介绍";
                break;
            case "custom1":
                property_text = "自定义1";
                break;
            case "custom2":
                property_text = "自定义2";
                break;
            case "custom3":
                property_text = "自定义3";
                break;
            case "custom4":
                property_text = "自定义4";
                break;
            case "custom5":
                property_text = "自定义5";
                break;
            case "managerName":
                property_text = "负责人";
                break;
            case "lastModifyTime":
                property_text = "修改时间";
                break;
        }
        return property_text;
    }

    public static String getSampleTouchStatus(int status) {
        String result = "";
        if (status == 0) {
            result = "失败";
        } else {
            result = "成功";
        }
        return result;
    }

    public static boolean hasPermission(Context cxt, String permission) {
        return ActivityCompat.checkSelfPermission(cxt, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean bitmapToFile(Bitmap bitmap, String filePath, String fileName) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File newFile = new File(filePath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(newFile);
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] bmpToByteArray(Bitmap bmp) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        return baos.toByteArray();
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }
    public static void url2Bitmap(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;

                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 32) { //循环判断如果压缩后图片是否大于32kb,大于继续压缩
            baos.reset(); //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos); //这里压缩options%，把压缩后的数据存放到baos中
            options -= 10; //每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray()); //把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null); //把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}
