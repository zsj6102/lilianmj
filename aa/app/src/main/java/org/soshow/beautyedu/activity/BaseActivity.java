package org.soshow.beautyedu.activity;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.utils.MyActivityManager;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if (SettingActivity.isMusic) {
        // if (HomeActivity.isHome) {
        // HomeActivity.playMusic();
        // HomeActivity.isHome = false;
        // }
        // if (HomeActivity.isOn) {
        // HomeActivity.playMusic();
        // HomeActivity.isOn = false;
        // }
        // }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slider_left_in,
                R.anim.anim_slider_right_out);
    }

}
