/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautyedu.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 
 * @Author: chenjiaming
 * @Time: 2015年6月30日 下午5:38:51
 * @Description: Toast提示文字
 */
public class ToastUtil {

    private static volatile ToastUtil toastUtil = null;
    private Toast toast = null;
    /** 显示Toast. */
    public static final int SHOW_TOAST = 0;

    public static ToastUtil getInstance() {
        if (toastUtil == null) {
            synchronized (ToastUtil.class) {
                if (toastUtil == null) {
                    toastUtil = new ToastUtil();
                }
            }
        }
        return toastUtil;
    }

    private ToastUtil() {
    }

    /**
     * 描述：Toast提示文本.
     * 
     * @param text 文本
     */
    public void showToast(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            if (toast != null) {
                toast.cancel();
            }
//            toast = new Toast(context);
//            View toastLayout = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
//            TextView tv = (TextView) toastLayout.findViewById(R.id.toast_view_tv);
//            tv.setText(text);
//            toast.setView(toastLayout);
//            toast.setDuration(Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 100);
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    /**
     * 描述：Toast提示文本.
     * 
     * @param resId 文本的资源ID
     */
    public void showToast(Context context, int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context,
                "" + context.getResources().getText(resId), Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 描述：在线程中提示文本信息.
     * 
     * @param resId 要提示的字符串资源ID
     */
    public void showToastInThread(final Activity context, final int resId) {
        context.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                showToast(context, resId);
            }
        });
    }

    /**
     * 描述：在线程中提示文本信息.
     * 
     * @param toast
     */
    public void showToastInThread(final Activity context, final String text) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(context, text);
            }
        });
    }

}
