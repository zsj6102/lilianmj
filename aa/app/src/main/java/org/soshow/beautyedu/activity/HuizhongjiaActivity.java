package org.soshow.beautyedu.activity;

import org.soshow.beautyedu.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
/**
 * 关于我们
 */
public class HuizhongjiaActivity extends BaseActivity {
	private TextView title_name;
	// private TextView app_setcontent;
	private TextView app_setversion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_huizhongjia);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
		title_name = (TextView) findViewById(R.id.title_name);

		app_setversion = (TextView) findViewById(R.id.app_setversion);
		title_name.setText("关于我们");

		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.puhuizhongjia.tongkao", 0);
			String vn = info.versionName;
			app_setversion.setText("V " + vn);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public void onClick(View v) {
		// Intent intent;

		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		default:
			break;
		}
	}

}
