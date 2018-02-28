/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautyedu.widget;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.utils.DensityUtil;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @Package: com.soshow.dfhon.view
 * @Author: chenjiaming
 * @Time: 2015年7月2日 上午11:30:47
 * @File: LoadingDialog.java
 * @Description: 加载中效果
 */
public class LoadingDialogShow {

    private static Dialog dlg;

    public static Dialog getInstance() {
        return dlg;
    }

    /**
     * @Author: chenjiaming
     * @Time: 2015年7月2日 下午1:37:58
     * @param activity
     * @ReturnType: Dialog
     * @Description: 加载dialog 默认文字 返回不能消失
     */
    public static Dialog loading(Activity activity) {
        return loading(activity,
                activity.getResources().getString(R.string.loading), false);
    }

    /**
     * 
     * @Author: chenjiaming
     * @Time: 2015年7月2日 下午2:10:31
     * @param activity
     * @param text 类型为int/String
     * @return
     * @ReturnType: Dialog
     * @Exception: None
     * @Description: 加载dialog 返回不能消失
     */
    public static Dialog loading(Activity activity, Object text) {
        return loading(activity, text, false);
    }

    /**
     * 
     * @Author: chenjiaming
     * @Time: 2015年7月2日 下午2:11:47
     * @param activity
     * @param cancelable 返回是否消失
     * @ReturnType: Dialog
     * @Description: 加载dialog 默认文字
     */
    public static Dialog loading(Activity activity, boolean cancelable) {
        return loading(activity,
                activity.getResources().getString(R.string.loading), cancelable);
    }

    /**
     * 
     * @Author: chenjiaming
     * @Time: 2015年7月2日 下午2:13:14
     * @param activity
     * @param text 类型为int/String 提示文字
     * @param cancelable 返回是否消失
     * @ReturnType: Dialog
     * @Description: 加载dialog
     */
    public static Dialog loading(Activity activity, Object text,
            boolean cancelable) {
        dlg = new Dialog(activity, R.style.loading_dialog);
        dlg.setCancelable(cancelable);
        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        View dlgView = View.inflate(activity, R.layout.view_loading_dialog,
                null);
        TextView tvText = (TextView) dlgView
                .findViewById(R.id.loadingDialog_tv_text);
        if (text instanceof java.lang.Integer) {
            tvText.setText(Integer.parseInt(text.toString()));
        } else if (text instanceof java.lang.String) {
            tvText.setText(text.toString());
        }
        dlg.setContentView(dlgView);
        Window dialogWindow = dlg.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = DensityUtil.getScreenMetrics(activity).x / 5 * 2;
        dialogWindow.setAttributes(lp);
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
        return dlg;
    }
    
    public static void hideLoading() {
        if (dlg != null && dlg.isShowing()) {
            dlg.dismiss();
        }
    }

}
