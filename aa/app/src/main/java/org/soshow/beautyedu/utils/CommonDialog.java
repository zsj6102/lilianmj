package org.soshow.beautyedu.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.soshow.beautyedu.R;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class CommonDialog extends Dialog {

    private Context context;
    private DialogOnClickListener listener;
    private TextView service_dialog_sure;
    private TextView service_dialog_cancle;

    public CommonDialog(Context context, String mainContent, String leftContent, String rightContent) {
        super(context, R.style.PostDialogTheme);
        this.context = context;
        initView(mainContent, leftContent, rightContent);
        initWindow();
    }

    private void initView(String mainContent, String leftContent, String rightContent) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_service, null);
        TextView dialog_service_phonenum =  (TextView) view.findViewById(R.id.dialog_service_phonenum);
        dialog_service_phonenum.setText(mainContent);
        service_dialog_sure = (TextView) view.findViewById(R.id.service_dialog_sure);
        service_dialog_sure.setText(leftContent);
        service_dialog_cancle = (TextView) view.findViewById(R.id.service_dialog_cancle);
        service_dialog_cancle.setText(rightContent);
        setContentView(view);
        service_dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.sureOnClick();
            }
        });
        service_dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancleOnClick();
            }
        });
    }

    private void initWindow() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();// 获取屏幕尺寸
        lp.width = (int) (d.widthPixels * 0.6); // 宽度为屏幕80%
        lp.gravity = Gravity.CENTER; // 中央居中
        dialogWindow.setAttributes(lp);
    }

    public void setCancelVisiable(int visiable) {
        service_dialog_cancle.setVisibility(visiable);
    }

    public void setListener(DialogOnClickListener listener) {
        this.listener = listener;
    }
}
