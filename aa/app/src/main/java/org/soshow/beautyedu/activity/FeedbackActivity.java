package org.soshow.beautyedu.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.json.bean.JsonResult;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 意见反馈
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.edit_feedback)
    EditText edit_feedback;
    @Bind(R.id.edit_phone)
    EditText phone;
    @Bind(R.id.btn_feedback)
    Button btn_feedback;
    TextView title_name;
    Editor editor;

    private String mToken;
    private String content;
    private SharedPreferences sp;
    private InputMethodManager imm;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
//		edit_feedback = (EditText) findViewById(R.id.edit_feedback);
//		phone = (EditText) findViewById(R.id.edit_phone);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("意见反馈");
        sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
        editor = sp.edit();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        dialog = ProgressDialogUtil.createLoadingDialog(FeedbackActivity.this, "发送中", true, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                try {
                    FeedbackActivity.this.getCurrentFocus();
                    FeedbackActivity.this.getCurrentFocus().getWindowToken();
                    imm.hideSoftInputFromWindow(FeedbackActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }
        return super.onTouchEvent(event);
    }
    @OnClick({ R.id.btn_feedback })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_feedback:
                String content = edit_feedback.getText().toString();
                String phonenumber = phone.getText().toString();
                submitFeedback(content, phonenumber);
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (TokenManager.handlered) {
                        Log.d("345abc", "获取新token-Feedback");
                        TokenManager.handlered = false;
                        mToken = sp.getString("mToken", null);
                        if (mToken != null) {
                            getInfo();
                        } else {
                            Toast.makeText(FeedbackActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // 需要做的事:发送消息
                                // LoginUtil.login_handlered = false;
                                Log.d("345abc", "循环等待-Feedback");
                                Message msg_loop = handler.obtainMessage();
                                msg_loop.what = 2;
                                msg_loop.sendToTarget();
                            }
                        }, 1000);
                    }
                    break;
                case 55:
                    finish();
                    overridePendingTransition(R.anim.anim_slider_left_in,
                            R.anim.anim_slider_right_out);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void submitFeedback(String content, String phonenumber) {
        if (StringUtil.isEmpty(content)) {
            Toast.makeText(FeedbackActivity.this, "请填写您的问题或建议", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!StringUtil.isEmpty(phonenumber) && !StringUtil.isPhone(phonenumber)) {
            Toast.makeText(FeedbackActivity.this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        this.content = content;
        getTokenLocal();

    }

    private void getTokenLocal() {

        mToken = new TokenManager(FeedbackActivity.this).getToken();
        if (mToken != null) {
            getInfo();
        } else {
            TokenManager.handlered = false;
            Message msg_loop = handler.obtainMessage();
            msg_loop.what = 2;
            msg_loop.sendToTarget();
        }
    }

    private void getInfo() {
        try {
            dialog.show();
            String app_nonce = StringUtil.getPhoneIMEI(FeedbackActivity.this);
            String content_new = URLEncoder.encode(content);
            String url_feedback;
            HashMap param = new HashMap();
            param.put("content", content_new);
            param.put("mobile", phone.getText().toString());
            NetHelper.get(Constant.phpUrl + "/wap/api.php?action=GET_FEEDBACK_INFO", param, new SimpleSingleBeanNetHandler<JsonResult>(FeedbackActivity.this) {
                @Override
                protected void onSuccess(JsonResult bean) {
                    if (bean.isSuccess()) {
                        Toast.makeText(FeedbackActivity.this, "您的意见或建议我们已收到，我们将尽快处理", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.anim_slider_left_in, R.anim.anim_slider_right_out);
                    }
                }

                @Override
                public void complete() {
                    hideProcess();
                }

                @Override
                protected void onError(int errorCode, String errorMsg) { //
                    Log.d("345abc", "Feedback错误信息为           " + errorCode + "             " + errorMsg);
                    dialog.hide();
                    Toast.makeText(FeedbackActivity.this, "发送失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
        }

    }

    protected void hideProcess() {
        runOnUiThread(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
        }
        try {
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
        }
        super.finish();
        overridePendingTransition(R.anim.anim_slider_left_in,
                R.anim.anim_slider_right_out);
    }

    @OnClick({R.id.edit_feedback, R.id.edit_phone, R.id.btn_feedback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_feedback:
                break;
            case R.id.edit_phone:
                break;
            case R.id.btn_feedback:
                break;
        }
    }

    @OnClick(R.id.btn_feedback)
    public void onViewClicked() {
    }
}
