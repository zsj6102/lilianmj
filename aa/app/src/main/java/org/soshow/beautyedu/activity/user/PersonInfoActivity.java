package org.soshow.beautyedu.activity.user;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautydu.photo.SelectPhoto;
import org.soshow.beautydu.photo.basepopup.utils.ToastUtils;
import org.soshow.beautydu.photo.localphoto.util.AlbumUtil;
import org.soshow.beautydu.photo.localphoto.util.CameraUtil;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.bean.PersonInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.JsonResult;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.PhotoUtils;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static org.soshow.beautydu.photo.SelectPhoto.REQEST_CODE_ALBUM;

public class PersonInfoActivity extends BaseActivity implements OnClickListener {

	private Editor editor;
	private String app_nonce;

	private String TAG = "PersonInfoActivity";
	private final static int GET_CROP_POTO = 1;
	public static boolean isInfoModify;
	public static boolean isChangeOver;
	public static boolean isOver;
	private TextView title_name;
	private SharedPreferences sp;
	private String mToken;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				dialog.dismiss();
			break;
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					Log.e(TAG, "handleMessage  token = " + mToken);
					if (mToken != null) {
						getInfo();
					} else {
						Toast.makeText(PersonInfoActivity.this, "网络不佳，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			default:
				break;
			}
		}

	};
	private ImageView iv;
	private EditText etNickname;
	private EditText etEmail;
	private EditText etPhone;
	private TextView tvSex;
	private EditText etSign;
	public static final int REQEST_CODE_CAMERA = 104; // 相机
	/**选中图片*/
	private String path;
	private ImageView ivBack;
	private View loading;
	private Context context;
	private PersonInfo personInfo;
	private Dialog dialog;
    private Uri imageUri;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
	private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
	private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;

	public static final int REQEST_CODE_CAMERA_RESULT = 105;// 相机结果
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_person_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_main);
		title_name = (TextView) findViewById(R.id.title_main);
		title_name.setText("个人信息");
		context = PersonInfoActivity.this;

		sp = PersonInfoActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		initView();
		getTokenLocal();
	}

	private void initView() {
		loading = (LinearLayout) findViewById(R.id.linear_load);
		loading.setVisibility(View.VISIBLE);
		ivBack = (ImageView) findViewById(R.id.back_search);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(this);
		TextView tvOver = (TextView) findViewById(R.id.over);
		tvOver.setVisibility(View.VISIBLE);
		tvOver.setOnClickListener(this);

		iv = (ImageView) findViewById(R.id.persin_info_iv);
		etNickname = (EditText) findViewById(R.id.person_info_tv_name);
		etEmail = (EditText) findViewById(R.id.person_info_tv_email);
		etPhone = (EditText) findViewById(R.id.person_info_tv_phone);
		tvSex = (TextView) findViewById(R.id.person_info_tv_sex);
		tvSex.setOnClickListener(this);
		etSign = (EditText) findViewById(R.id.person_info_tv_note);
		findViewById(R.id.rl_change_head).setOnClickListener(this);
	}

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(PersonInfoActivity.this).getToken();
		Log.e(TAG, "getTokenLocal  token = " + mToken);
		if (mToken != null) {
			getInfo();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	// 获取个人资料数据
	private void getInfo() {
		try {
			String url_my = Constant.PERSON_INFO;
//					+ "&tocken=" + mToken
//					+ "&app_nonce=" + app_nonce;
			Log.e(TAG, "个人资料url=" + url_my);
			NetHelper.get(url_my,new SimpleSingleBeanNetHandler<Captcha>(this) {
				@Override
				protected void onSuccess(Captcha bean) {
					String result = bean.result;
					if (result.equals("0")) {
						String data = bean.info;
						LogUtils.e(TAG, " 数据======" + data.toString());
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(data);
							personInfo = GsonUtils.parseJSON( jsonObject.toString(), PersonInfo.class);
							UniversalImageLoadTool.disPlayTrue( personInfo.photo_url, iv,
									R.drawable.default_face);
							etNickname.setText(personInfo.getNickname());
							etEmail.setText(personInfo.getEmail());
							etPhone.setText(personInfo.mobile);
							etPhone.setEnabled(false);//不可修改手机
							etSign.setText(personInfo.getSignature());
							String sex = personInfo.getGender();
							if (sex.equals("0")) {
								tvSex.setText("保密");
							} else if (sex.equals("1")) {
								tvSex.setText("男");
							} else if (sex.equals("2")) {
								tvSex.setText("女");
							} else {
								tvSex.setText("");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						loading.setVisibility(View.GONE);
					} else {
						ToastUtil.getInstance().showToast(context, bean.message);
					}
				}
			});
		} catch (Exception e) {
			Log.e("","",e);
		}

	}

	// 修改个人资料
	private void modifyInfo() {
		dialog = ProgressDialogUtil.createLoadingDialog(this, "", true, false);
		dialog.show();
		try {
			HashMap param = new HashMap();
			{	//params
				if (!TextUtils.isEmpty(etNickname.getText().toString().trim())) {
					param.put("nickname", etNickname.getText().toString());
				}
				if (!TextUtils.isEmpty(etEmail.getText().toString().trim())) {
					param.put("email", etEmail.getText().toString());
				}
				if (!TextUtils.isEmpty(etSign.getText().toString().trim())) {
					param.put("signature", etSign.getText().toString());
				}
				if (!TextUtils.isEmpty(tvSex.getText().toString().trim())) {
					String sex = tvSex.getText().toString().trim();
					if (sex.equals("保密")) {
						sex = "0";
					} else if (sex.equals("男")) {
						sex = "1";
					} else if (sex.equals("女")) {
						sex = "2";
					}
					param.put("gender", sex);
				}
				if (!TextUtils.isEmpty(path)) {
					param.put("photo", new File(path));
				}
			}
			NetHelper.postUpload(Constant.MODIFY_UESR_INFO, param, new SimpleSingleBeanNetHandler<JsonResult>(this) {
				@Override
				protected void onSuccess(JsonResult ret) {
					if (ret.isSuccess()) {
						isInfoModify = true;
						if (!TextUtils.isEmpty(path)) {
							SPUtils.put(PersonInfoActivity.this,"headUrl",path);
							isChangeOver = true;
						}
						 if(!TextUtils.isEmpty(etNickname.getText().toString().trim())){
							 SPUtils.put(PersonInfoActivity.this,"nickname",etNickname.getText().toString());
						 }
						finish();
					}else{
						ToastUtil.getInstance().showToast( PersonInfoActivity.this, ret.getMessage_() );
					}
				}
				@Override
				public void complete() {
					hideProcess();
					super.complete();
				}
			});
		}finally {
		}
	}

	/***/
	private void hideProcess() {
		runOnUiThread(new Runnable() {
            public void run() {
                dialog.hide();
            }
        });
	}

	@Override
	public void finish() {
		super.finish();
		try {
			handler.removeCallbacksAndMessages(null);
		} catch (Exception e) {
		}
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.over:// 完成
			String name = etNickname.getText().toString().trim();
			String email = etEmail.getText().toString().trim();
			String phone = etPhone.getText().toString().trim();
			String sex = tvSex.getText().toString();
			String note = etSign.getText().toString().trim();
			if (sex.equals("")) {
				sex = "0";
			} else if (sex.equals("男")) {
				sex = "1";
			} else if (sex.equals("女")) {
				sex = "2";
			}
			
			if (!(path == null && name.equals(personInfo.getUsername())
					&& email.equals(personInfo.getEmail())
					&& phone.equals(personInfo.mobile)
					&& sex.equals(personInfo.getGender())
					&& note.equals(personInfo.getSignature()))) {
				modifyInfo();
			}
			break;
		case R.id.rl_change_head:// 更换头像
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String[] itemsId = context.getResources().getStringArray(
                    R.array.select_photoes);
            builder.setItems(itemsId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            // 拍照
                            autoObtainCameraPermission();
                            break;
                        case 1:
                            // 从相册
							autoObtainStoragePermission();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.show();
			break;
		case R.id.person_info_tv_sex:
			genderSelect();
			break;

		default:
			break;
		}

	}
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(PersonInfoActivity.this,"您已经拒绝过一次",Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (org.soshow.beautyedu.utils.SdcardUtil.sdCardIsExit()) {
                imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(PersonInfoActivity.this, "com.zz.fileprovider", fileUri);
                }
                PhotoUtils.takePicture(this, imageUri, REQEST_CODE_CAMERA);
            } else {
				Toast.makeText(this,"设备没有SD卡！",Toast.LENGTH_SHORT).show();
            }
        }
    }
	private void autoObtainStoragePermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
		} else {
			PhotoUtils.openPic(this, REQEST_CODE_ALBUM);
		}

	}
	/**
	 * 性别选择
	 */
	private void genderSelect() {
		final String[] itemArray = getResources()
				.getStringArray(R.array.gender);
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.sex);
		dialog.setItems(R.array.gender,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tvSex.setText(itemArray[which]);
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case REQEST_CODE_ALBUM:// 相册
			if (data == null) {
				break;
			}
			if (org.soshow.beautyedu.utils.SdcardUtil.sdCardIsExit()) {
				path = AlbumUtil.getPath(this,data.getData());
			} else {
				Toast.makeText(this,"设备没有SD卡！",Toast.LENGTH_SHORT).show();
			}
			break;

		case  REQEST_CODE_CAMERA:// 相机
			path = AlbumUtil.getPath(this,imageUri);
			break;

		// case GET_CROP_POTO:
		//
		// cropPath = data.getStringExtra("CropFile");
		// BitmapFactory.Options opt = new BitmapFactory.Options();
		// opt.inSampleSize = 6;
		// bitmap = BitmapFactory.decodeFile(cropPath, opt);
		// iv.setImageBitmap(bitmap);
		//
		// return;

		}

		if (path == null || path.trim().length() == 0) {
			LogUtils.e("获取图片失败！图片路径为空！！");
			finish();
		}
		int degree = readPictureDegree(path);
		Bitmap bitmap = adjust(path);
		if (bitmap == null) {
			ToastUtil.getInstance().showToast(this, "图片数据异常或图片不存在");
			finish();
			return;
		}

		bitmap = rotaingImageView(degree, bitmap);
		iv.setImageBitmap(bitmap);

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			//调用系统相机申请拍照权限回调
			case CAMERA_PERMISSIONS_REQUEST_CODE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (org.soshow.beautyedu.utils.SdcardUtil.sdCardIsExit()) {
						imageUri = Uri.fromFile(fileUri);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
							imageUri = FileProvider.getUriForFile(PersonInfoActivity.this, "com.zz.fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
						PhotoUtils.takePicture(this, imageUri, REQEST_CODE_CAMERA);
					} else {
						Toast.makeText(this,"设备没有SD卡！",Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this,"请允许打开相机！",Toast.LENGTH_SHORT).show();
				}
				break;
			}
			//调用系统相册申请Sdcard权限回调
			case STORAGE_PERMISSIONS_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					PhotoUtils.openPic(this, REQEST_CODE_ALBUM);
				} else {
					Toast.makeText(this,"请允许打操作SDCard！",Toast.LENGTH_SHORT).show();
				}
				break;
			default:
		}
	}
	@Override
	protected void onDestroy() {
		if(dialog!=null){
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			dialog = null;
		}
		super.onDestroy();
	}

	// --------------------------------图片处理---------------------------------------

	// 获取图片旋转角度
	private int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	protected Bitmap adjust(String path) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;

		Bitmap bitmap = BitmapFactory.decodeFile(path, opt);

		int w = opt.outWidth;
		int h = opt.outHeight;

		int screenWidth = DensityUtil.getScreenMetrics(this).x;
		int screenHeight = DensityUtil.getScreenMetrics(this).y;

		opt.inSampleSize = 1;
		// 根据屏的大小和图片大小计算出缩放比例
		if (w > h) {
			if (w > screenWidth)
				opt.inSampleSize = w / screenWidth;
		} else {
			if (h > screenHeight)
				opt.inSampleSize = h / screenHeight;
		}

		opt.inJustDecodeBounds = false;

		bitmap = BitmapFactory.decodeFile(path, opt);

		return bitmap;
	}

	// 旋转图片
	private Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		// bitmap.recycle();
		return resizedBitmap;
	}

	// ------------------------------------END-----------------------------------------
}
