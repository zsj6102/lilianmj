/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautyedu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import org.soshow.beautyedu.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.widget.Toast;

/**
 * 
 * @Author: chenjiaming
 * @Time: 2015年6月30日 下午5:59:25
 * @Description: None
 */
public class SdcardUtil {

    /**
     * @return 返回SD卡是否存在
     * @Description 用于判断SD卡是否存在
     */
    public static boolean sdCardIsExit() {
        return Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * @return 返回SD卡路径
     * @Description 当SD卡存在时，返回SD卡路径，否则返回空值
     */
    public static String getSDCardPath() {
        if (sdCardIsExit()) {
            return Environment.getExternalStorageDirectory().toString()
                    + File.separator;
        }
        return null;
    }

    /**
     * 
     * @param context
     * @param filename
     * @param bitmap
     * @Description: 将bitmap保存到内部存储空间
     */
    public static void saveBitmapApp(Context context, String filename,
            Bitmap bitmap) {
        File file = new File(context.getFilesDir(), filename);
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, os);
            os.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 
     * @Author: chenjiaming
     * @Time: 2015年5月26日 上午9:21:13
     * @param context
     * @param filename
     * @param bitmap
     * @ReturnType: void
     * @Exception: None
     * @Description: 保存bitmap压缩后的图片，到内部存储空间
     */
    public static void saveBitmapAppCompress(Context context, String filename,
            Bitmap bitmap) {
        File file = new File(context.getFilesDir(), filename);

        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, os);
            os.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * @param dirPath 用于创建目录的字符串目录
     * @return 返回创建好的SD卡目录
     * @Description 用来创建SD卡下的目录，必须在判断SD卡存在后执行
     */
    private static String creatDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dirPath;
    }

    /**
     * @param context 上下文环境
     * @param bitmap 用来保存到SD卡的bitmap
     * @param path 用来保存到SD卡的路径
     * @return 返回是否保存成功，true表示保存成功，false表示保存失败，SD卡不存在
     * @Description 用来将bitmap保存到SD卡的指定目录下，以时间戳和jpg的方式。
     */
    public static boolean saveBitmapToSdInJpg(Context context, Bitmap bitmap,
            String path) {
        String dfhonDir = SdcardUtil.getSDCardPath() + path;
        StringBuilder imgFile = new StringBuilder();
        boolean saveFlag = false;
        if (!SdcardUtil.sdCardIsExit()) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.without_sdcard),
                    Toast.LENGTH_SHORT).show();
            return saveFlag;
        }
        SdcardUtil.creatDir(dfhonDir);
        CharSequence currTime = DateFormat.format("yyyy-MM-dd-hh-mm-ss",
                new Date());
        imgFile.append(currTime);
        imgFile.append(".jpg");
        File file = new File(dfhonDir, imgFile.toString());
        try {
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, os);
            saveFlag = true;

            // 发送广播，更新相册中的图片
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
            } else {
                context.sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                                .parse("file://" + path)));
            }
            ToastUtil.getInstance().showToast(
                    context,
                    context.getResources().getString(
                            R.string.save_picture_success));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return saveFlag;
    }

    /**
     * @Author: chenjiaming
     * @Time: 2015年5月12日 上午10:37:42
     * @param context 上下文环境
     * @param bitmap 用来保存到SD卡的bitmap
     * @param path 用来保存到SD卡的路径
     * @param imgName 用来保存到SD卡的图片名字
     * @return 返回是否保存成功，true表示保存成功，false表示保存失败，SD卡不存在
     * @ReturnType: boolean
     * @Exception: None
     * @Description: 用来将bitmap保存到SD卡的指定目录下，以指定名字和jpg的方式。
     */
    public static boolean saveBitmapToSdInJpg(Context context, Bitmap bitmap,
            String path, String imgName) {
        String fileDir = SdcardUtil.getSDCardPath() + path;
        StringBuilder imgFile = new StringBuilder();
        boolean saveFlag = false;
        if (!SdcardUtil.sdCardIsExit()) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.without_sdcard),
                    Toast.LENGTH_SHORT).show();
            return saveFlag;
        }
        SdcardUtil.creatDir(fileDir);
        imgFile.append(imgName);
        imgFile.append(".jpg");
        File file = new File(fileDir, imgFile.toString());
        try {
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, os);
            saveFlag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return saveFlag;
    }

    /**
     * @Author: zhengqinglian
     * @Time: 2015年7月14日 上午10:12:48
     * @param f
     * @return
     * @ReturnType: int
     * @Exception: None
     * @Description: 获取单个文件的大小
     */
    private static int getFileSize(File f) {
        if (f == null || !f.exists()) {
            return 0;
        }

        try {
            FileInputStream fis = new FileInputStream(f);
            return fis.available();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * @Author: zhengqinglian
     * @Time: 2015年7月14日 上午10:12:48
     * @param f
     * @return
     * @ReturnType: int
     * @Exception: None
     * @Description: 获取文件加夹的大小
     */
    private static int getFilesSize(File f) {
        int size = 0;
        if (f == null || !f.exists()) {
            return size;
        }

        File[] fileList = f.listFiles();

        for (int i = 0; i < fileList.length; i++) {
            File temp = fileList[i];

            if (!temp.exists()) {
                continue;
            }

            if (temp.isFile()) {
                size += getFileSize(temp);
                continue;
            }

            if (temp.isDirectory()) {
                size += getFilesSize(temp);
                continue;
            }
        }

        return size;
    }

    /**
     * @Author: zhengqinglian
     * @Time: 2015年7月14日 上午10:26:48
     * @param context
     * @return
     * @ReturnType: int
     * @Exception: None
     * @Description: 获取缓存大小
     */
    public static int getCacheSize(Context context) {
        File cd = context.getCacheDir();
        File ecd = context.getExternalCacheDir();
        File fd = context.getFilesDir();
        File ecfd = context.getExternalFilesDir(null);
        int size = getFilesSize(cd);
        size += getFilesSize(ecd);
        size += getFilesSize(fd);
        size += getFilesSize(ecfd);
        return size;
    }

    public static String valueToString(int size) {
        String sizeText = "";
        DecimalFormat d = new DecimalFormat("0.0");
        if (size < 512) {
            sizeText = size + "Byte";
        } else if (size < 512 * 1024) {

            sizeText = d.format(size / 1024f) + "KB";
        } else {
            sizeText = d.format(size / 1024f / 1024f) + "MB";
        }

        return sizeText;
    }

    private static void deleteDir(File dir) {
        if (dir == null) {

            return;
        }
        File[] filelist = dir.listFiles();
        if (filelist == null) {

            return;
        }
        if (filelist.length == 0) {
            dir.delete();
            return;
        }
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory()) {
                deleteDir(filelist[i]);
                continue;
            } else {
                filelist[i].delete();
            }
        }
        dir.delete();
    }

    public static void deleteCache(Activity context) {
        final File cd = context.getCacheDir();
        final File ecd = context.getExternalCacheDir();
        final File fd = context.getFilesDir();
        final File ecfd = context.getExternalFilesDir(null);
//        final Dialog d = ProgressDialog.get(context, R.string.clearing);
//        d.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                deleteDir(cd);
                deleteDir(ecd);
                deleteDir(fd);
                deleteDir(ecfd);
//                d.dismiss();
            }
        }, 2000);
    }

}
