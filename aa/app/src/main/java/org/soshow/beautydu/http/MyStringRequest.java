/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautydu.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

/**
 * 
 * @Author: huangenqiang
 * @Time: 2015年8月31日 上午9:40:31
 * @Description: 自定义StringRequest请求类
 */
public class MyStringRequest extends StringRequest {

    private Context context;
    private Map<String, String> params;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    public MyStringRequest(Context context, int method, String url,
            Map<String, String> params, Listener<String> listener,
            ErrorListener errorListener) {

        super(method, url, listener, errorListener);
        this.context = context;
        this.params = params;

    }

    public MyStringRequest(Context context, int method, String url,
            Listener<String> listener, ErrorListener errorListener) {

        super(method, url, listener, errorListener);
        this.context = context;

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    /**
     * 请求头设置
     */

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> header = new HashMap<String, String>();
        String lan = Locale.getDefault().getLanguage();
        String type = android.os.Build.MODEL;
        String version;
        if (lan.startsWith("zh")) {
            lan = "zh";
        }
        if (lan.startsWith("en")) {
            lan = "en";
        }

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            version = info.versionName;
            header.put("client_language", lan);
            header.put("client_version", version);
            header.put("client_platform", "android");
            header.put("client_type", type);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return header;
    }

}
