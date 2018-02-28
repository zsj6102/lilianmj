package org.soshow.beautyedu.activity;

import org.soshow.beautyedu.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SettingActivity2 extends FragmentActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		
	}


	@Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slider_left_in,
                R.anim.anim_slider_right_out);
    }

	

	

}
