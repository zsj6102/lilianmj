package org.soshow.beautydu.photo.localphoto.util;

import java.io.File;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * 拍照、取图工具类
 * 
 * @author GuiLin
 */
public class CameraUtil {

	public static String cameraDir = getSDCardPath() + "dfhon/Camera/";
	private static StringBuilder fileName = new StringBuilder();

	/**
	 * 获取拍照的Intent
	 * 
	 * @return
	 */
	public static Intent getCameraIntent() {
		resetStringBuilder();
		fileName.append(getCurrentTime());
		fileName.append(".jpg");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		creatDir(cameraDir);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(cameraDir, fileName.toString())));
		return intent;
	}

	/**
	 * 获取相册intent
	 * 
	 * @return
	 */
	public static Intent getAlbumIntent() {
		Intent intent;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
					"image/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
//			intent = new Intent(Intent.ACTION_PICK);
			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
					"image/*");
			intent.setType("image/*");
		}
		return intent;
	}

	/**
	 * 获取当前时间<br>
	 */
	private static CharSequence getCurrentTime() {
		return DateFormat.format("yyyy-MM-dd-hh-mm-ss", new Date());
	}

	/**
	 * 重置fileName
	 */
	private static void resetStringBuilder() {
		if (fileName.length() > 0) {
			fileName.delete(0, fileName.length());
		}
	}

	/**
	 * 获取图片真实路径
	 */
	public static String getRealFilePath() {
		return cameraDir + fileName.toString();
	}

	/**
	 * 创建文件夹
	 */
	private static String creatDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dirPath;
	}

	/**
	 * 判断SD卡是否可用
	 */
	public static boolean sdCardIsExit() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡路径
	 */
	public static String getSDCardPath() {
		if (sdCardIsExit()) {
			return Environment.getExternalStorageDirectory().toString() + "/";
		}
		return null;
	}
}
