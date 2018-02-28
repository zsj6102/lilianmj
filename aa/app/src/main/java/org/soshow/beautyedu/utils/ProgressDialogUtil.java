package org.soshow.beautyedu.utils;

import org.soshow.beautyedu.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProgressDialogUtil {
	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg, Boolean dim, boolean cancelable) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
		// 使用ImageView显示动画

		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		if (msg != null) {
			tipTextView.setText(msg);// 设置加载信息
		}
		Dialog loadingDialog;
		if (dim) {
			loadingDialog = new Dialog(context, R.style.loading_dialog_dim);
		} else {
			loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		}
		if (cancelable) {
			// loadingDialog.setCancelable(true);
			loadingDialog.setCanceledOnTouchOutside(true);
		} else {
			loadingDialog.setCanceledOnTouchOutside(false);
			// loadingDialog.setCancelable(false);

		}
		loadingDialog.setCancelable(true);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;

	}
}
