package org.soshow.beautyedu.login.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 日志工具
 * 
 * 
 * 
 */
public class LOG {

	public static final boolean DEBUG = true;

	public static void cstdr(String TAG, String msg) {
		if (DEBUG) {
			Log.i("cstdr", TAG + "~~~" + msg);
		}
	}

	public static void showToast(final Context mContext, final String content) {
		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast toast = Toast.makeText(mContext, content,
						Toast.LENGTH_SHORT);
				toast.show();

			}
		});

	}

	public static void setTextViewContent(final Activity activity,
			final TextView textView, final String content) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (textView != null) {
					textView.setText(content);
				}

			}
		});

	}
}
