/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautydu.http;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.widget.LoadingDialogShow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * 
 * @Author: huangenqiang
 * @Time: 2015年8月26日 上午9:40:09
 * @Description: http请求工具类
 */
public class HttpUtils {
    private static RequestQueue requestQueue;
    private static int SOCKET_TIMEOUT = 20 * 1000;
    private static RetryPolicy retryPolicy = new DefaultRetryPolicy(
            SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private HttpUtils() {
    }

    /**
     * 初始化请求队列
     */
    private static void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);

    }

    /**
     * post 请求
     * 
     * @param <T>
     */
    public static void post(final Context context, String url,
            final Map<String, String> params, final VolleyListener listener) {

        StringRequest request = new MyStringRequest(context, Method.POST, url,
                params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String obj) {
                        dialogDismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(obj);
                            int result = jsonObject.getInt("result");
                            String message = jsonObject.getString("message");
                            if (result == 0) {
                                listener.onResponse(obj);
                            } else if (result == 99) {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                                Intent intent = new Intent(context,
                                        LoginInputActivity.class);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
//                                MyActivityManager.getInstance()
//                                        .finishAllActivity();
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.getInstance().showToast(context,
                                    R.string.netreques_error);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.getInstance().showToast(context,
                                VolleyErrorHelper.getMessage(error, context));
                        dialogDismiss();
                    }

                });
        if (requestQueue == null) {
            init(context);
        }
        // 设置请求超时时间
        request.setRetryPolicy(retryPolicy);
        // 设置缓存
        request.setShouldCache(true);
        requestQueue.add(request);
    }

    /**
     * post 请求
     * 
     * @param <T>
     */
    public static <T> void post(final Context context, String url,
            final Map<String, String> params, final LinearLayout linearLoading,
            final VolleyListener listener) {

        StringRequest request = new MyStringRequest(context, Method.POST, url,
                params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String obj) {
                        if (linearLoading != null) {
                            linearLoading.setVisibility(View.GONE);
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(obj);
                            int result = jsonObject.getInt("result");
                            String message = jsonObject.getString("message");
                            if (result == 0) {
                                listener.onResponse(obj);
                            } else if (result == 99) {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                                Intent intent = new Intent(context,
                                        LoginInputActivity.class);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
//                                MyActivityManager.getInstance()
//                                        .finishAllActivity();
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.getInstance().showToast(context,
                                    R.string.netreques_error);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ToastUtil.getInstance().showToast(context,
                                VolleyErrorHelper.getMessage(error, context));
                        if (linearLoading != null) {
                            linearLoading.setVisibility(View.GONE);
                        }
                    }

                });
        if (requestQueue == null) {
            init(context);
        }
        // 设置请求超时时间
        request.setRetryPolicy(retryPolicy);
        // 设置缓存
        request.setShouldCache(true);
        requestQueue.add(request);
    }

    /**
     * get 请求
     */
    public static void get(final Context context, final String url,
            final LinearLayout linearLoading, final VolleyListener listener) {
        StringRequest request = new MyStringRequest(context, Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String obj) {
                        if (linearLoading != null) {
                            linearLoading.setVisibility(View.GONE);
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(obj);
                            int result = jsonObject.getInt("result");
                            String message = jsonObject.optString("message");
                            if (result == 0) {
                                listener.onResponse(obj);
                            } else if (result == 99) {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                                Intent intent = new Intent(context,
                                        LoginInputActivity.class);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
//                                MyActivityManager.getInstance()
//                                        .finishAllActivity();
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.getInstance().showToast(context,
                                    R.string.netreques_error);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ToastUtil.getInstance().showToast(context,
                                VolleyErrorHelper.getMessage(error, context));
                        if (linearLoading != null) {
                            linearLoading.setVisibility(View.GONE);
                        }
                    }

                });
        if (requestQueue == null) {
            init(context);
        }
        // 设置请求超时时间
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);

    }

    /**
     * get 请求
     */
    public static void get(final Context context, final String url,
            final VolleyListener listener) {
        StringRequest request = new MyStringRequest(context, Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String obj) {
                        dialogDismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(obj);
                            int result = jsonObject.getInt("result");
                            String message = jsonObject.optString("message");
                            if (result == 0) {
                                listener.onResponse(obj);
                            } else if (result == 99) {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                                Intent intent = new Intent(context,
                                        LoginInputActivity.class);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
//                                MyActivityManager.getInstance()
//                                        .finishAllActivity();
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.getInstance().showToast(context,
                                    R.string.netreques_error);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.getInstance().showToast(context,
                                VolleyErrorHelper.getMessage(error, context));
                        dialogDismiss();
                    }

                });
        if (requestQueue == null) {
            init(context);
        }
        // 设置请求超时时间
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);

    }

    /**
     * get 请求
     */
    public static void get(final Context context, final String url,
            final Map<String, String> params, final VolleyListener listener) {
        StringRequest request = new MyStringRequest(context, Method.GET, url,
                params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String obj) {
                        dialogDismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(obj);
                            int result = jsonObject.getInt("result");
                            String message = jsonObject.getString("message");
                            if (result == 0) {
                                listener.onResponse(obj);
                            } else if (result == 99) {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                                Intent intent = new Intent(context,
                                        LoginInputActivity.class);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
//                                MyActivityManager.getInstance()
//                                        .finishAllActivity();
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.getInstance().showToast(context,
                                    R.string.netreques_error);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.getInstance().showToast(context,
                                VolleyErrorHelper.getMessage(error, context));
                        dialogDismiss();
                    }

                });
        if (requestQueue == null) {
            init(context);
        }
        // 设置请求超时时间
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);

    }

    /**
     * delete 请求
     */
    public static void delete(final Context context, String url,
            final VolleyListener listener) {
        StringRequest request = new MyStringRequest(context, Method.DELETE,
                url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String obj) {
                        dialogDismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(obj);
                            int result = jsonObject.getInt("result");
                            String message = jsonObject.getString("message");
                            if (result == 0) {
                                listener.onResponse(obj);
                            } else if (result == 99) {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                                Intent intent = new Intent(context,
                                        LoginInputActivity.class);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
//                                MyActivityManager.getInstance()
//                                        .finishAllActivity();
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.getInstance().showToast(context,
                                    R.string.netreques_error);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.getInstance().showToast(context,
                                VolleyErrorHelper.getMessage(error, context));
                        dialogDismiss();
                    }

                });
        if (requestQueue == null) {
            init(context);
        }
        // 设置请求超时时间
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);

    }

    /**
     * delete 请求
     */
    public static void delete(final Context context, String url,
            final LinearLayout linearLoading, final VolleyListener listener) {
        StringRequest request = new MyStringRequest(context, Method.DELETE,
                url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String obj) {
                        dialogDismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(obj);
                            int result = jsonObject.getInt("result");
                            String message = jsonObject.getString("message");
                            if (result == 0) {
                                listener.onResponse(obj);
                            } else if (result == 99) {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                                Intent intent = new Intent(context,
                                        LoginInputActivity.class);
                                ((Activity) context).startActivity(intent);
                                ((Activity) context).finish();
//                                MyActivityManager.getInstance()
//                                        .finishAllActivity();
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.getInstance().showToast(context,
                                    R.string.netreques_error);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.getInstance().showToast(context,
                                VolleyErrorHelper.getMessage(error, context));
                        if (linearLoading != null) {
                            linearLoading.setVisibility(View.GONE);
                        }
                    }

                });
        if (requestQueue == null) {
            init(context);
        }
        // 设置请求超时时间
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);

    }

    private static void dialogDismiss() {
        LoadingDialogShow.hideLoading();
    }

    public static RequestQueue getRequestQueue(Context context) {
        if (requestQueue != null) {
            return requestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }
}