package org.soshow.beautydu.photo;

import org.soshow.beautydu.photo.localphoto.SelectPhotoActivity;
import org.soshow.beautydu.photo.localphoto.util.AlbumUtil;
import org.soshow.beautydu.photo.localphoto.util.CameraUtil;

import org.soshow.beautyedu.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class SelectPhoto {
    public static final int TAKE_PHOTO = 0;
    public static final int CHOOSE_PHOTO = 1;

    public static final int REQEST_CODE_ALBUM = 101; // 相册
    public static final int REQEST_CODE_CROP = 102; // 剪裁
    public static final int REQEST_CODE_CROP_RESULT = 103;// 剪裁结果
    


    public static final int CODE_CAMERA = 21;
    public static final int CODE_PHOTO = 22;
    public static final int DELETE_IMAGE = 30;

    private static SelectPhoto selectPhoto;
    private Context context;

    private SelectPhoto(Context context) {
        this.context = context;
    }

    public static SelectPhoto getInstance(Context context) {
        if (selectPhoto == null) {
            synchronized (SelectPhoto.class) {
                if (selectPhoto == null) {
                    selectPhoto = new SelectPhoto(context);
                }
            }
        }
        return selectPhoto;
    }
    
    /**
     * 选择图片方式，拍照、相册
     * @param activity 只有一个Activity才能添加一个窗体
     */
    public void selectPhotoWay(final Activity activity) {

        final boolean sdCardIsExit = org.soshow.beautyedu.utils.SdcardUtil.sdCardIsExit();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String[] itemsId = context.getResources().getStringArray(
                R.array.select_photoes);
        builder.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:
                    // 拍照
//                    if (sdCardIsExit) {
//
//                        activity.startActivityForResult(
//                                CameraUtil.getCameraIntent(),
//                                REQEST_CODE_CAMERA);
//                        activity.overridePendingTransition(R.anim.anim_slider_right_in,
//            	                R.anim.anim_slider_left_out);
//
//                    } else {
//                        Toast.makeText(
//                                context,
//                                context.getResources().getString(
//                                        R.string.without_sdcard),
//                                Toast.LENGTH_SHORT).show();
//                    }
                    break;
                case 1:
                    // 从相册
                    Intent intent = CameraUtil.getAlbumIntent();
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        activity.startActivityForResult(intent,
                                REQEST_CODE_ALBUM);
                        activity.overridePendingTransition(R.anim.anim_slider_right_in,
            	                R.anim.anim_slider_left_out);
                    } else {
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(intent,
                                REQEST_CODE_ALBUM);
                        activity.overridePendingTransition(R.anim.anim_slider_right_in,
            	                R.anim.anim_slider_left_out);
                    }
                    break;
                case 2:
                    dialog.dismiss();
                    break;
                }
            }
        });
        builder.show();
    }


    /**
     * 选择图片方式，拍照、相册
     * @param activity 只有一个Activity才能添加一个窗体
     * @param sdCardIsExit 是否有sd卡
     */
    public void selectPhotoWay(final Activity activity,
            final boolean sdCardIsExit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String[] itemsId = context.getResources().getStringArray(
                R.array.select_photoes);
        builder.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:
                    // 拍照
                    if (sdCardIsExit) {
                        activity.startActivityForResult(
                                CameraUtil.getCameraIntent(), REQEST_CODE_CROP);
                    } else {
                        Toast.makeText(
                                context,
                                context.getResources().getString(
                                        R.string.no_sdCard), Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case 1:
                    // 从相册
                    activity.startActivityForResult(
                            CameraUtil.getAlbumIntent(), REQEST_CODE_ALBUM);
                    break;
                case 2:
                    dialog.dismiss();
                    break;
                }
            }
        });
        builder.show();
    }

    /**
     * 方法重载 选择图片方式，拍照、相册
     * @param activity 只有一个Activity才能添加一个窗体
     * @param sdCardIsExit 是否有sd卡
     */
    public void selectPhotoWay(final Activity activity,
            final boolean sdCardIsExit, int which) {
        switch (which) {
        case 0:
            // 拍照
            if (sdCardIsExit) {
                activity.startActivityForResult(CameraUtil.getCameraIntent(),
                        REQEST_CODE_CROP);
            } else {
                Toast.makeText(context,
                        context.getResources().getString(R.string.no_sdCard),
                        Toast.LENGTH_SHORT).show();
            }
            break;
        case 1:
            // 从相册
            Intent intent = CameraUtil.getAlbumIntent();
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                activity.startActivityForResult(intent, REQEST_CODE_ALBUM);
            } else {
                intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(intent, REQEST_CODE_ALBUM);
            }
            break;
        }
    }

    /**
     * 获取相册图片路径 通过判断版本号，忽略google相册读取不到路径
     * @param data
     * @return 图片路径
     */
    @SuppressLint("NewApi")
    public String photoAlbumPath(Intent data) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String fileName = null;
        Uri originalUri = data.getData();
        if (isKitKat && DocumentsContract.isDocumentUri(context, originalUri)) {
            fileName = AlbumUtil.getPath(context, originalUri);
        } else {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(originalUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    fileName = cursor.getString(cursor.getColumnIndex("_data"));
                } while (cursor.moveToNext());
            } else if (cursor == null) {
                fileName = AlbumUtil.getPath(context, originalUri);
            }
        }
        return fileName;
    }

    /**
     * 选择图片方式，跳转多图选择
     */
    public void selectPictureStyle(final Activity activity,
            final boolean sdCardIsExit) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        String[] photoArr = context.getResources().getStringArray(
                R.array.select_photoes);
        dialog.setItems(photoArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                case 0://拍照
                    if (sdCardIsExit) {
                        activity.startActivityForResult(
                                CameraUtil.getCameraIntent(), CODE_CAMERA);
                        activity.overridePendingTransition(R.anim.anim_slider_right_in,
                                R.anim.anim_slider_left_out);
                    } else {
                        Toast.makeText(context,context.getResources().getString(R.string.no_sdCard), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1://相册
                    intent = new Intent(context, SelectPhotoActivity.class);
                    activity.startActivityForResult(intent, CODE_PHOTO);
                    activity.overridePendingTransition(R.anim.anim_slider_right_in,
                            R.anim.anim_slider_left_out);
                    break;
                case 2://取消
                    dialog.dismiss();
                    break;
                default:
                    break;
                }
            }
        });
        dialog.show();
    }
}
