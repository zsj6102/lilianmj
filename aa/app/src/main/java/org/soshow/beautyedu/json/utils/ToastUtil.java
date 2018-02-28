package org.soshow.beautyedu.json.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void prompt(Context context, String prompt) {
		Toast.makeText(context, prompt, Toast.LENGTH_SHORT).show();
	}

	public static void longPrompt(Context context, String prompt) {
		Toast.makeText(context, prompt, Toast.LENGTH_LONG).show();
	}

}
