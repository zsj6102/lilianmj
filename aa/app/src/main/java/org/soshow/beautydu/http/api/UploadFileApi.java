package org.soshow.beautydu.http.api;

import java.io.File;
import java.util.Map;

import org.json.JSONObject;
import org.soshow.beautydu.http.MultiPartStack;
import org.soshow.beautydu.http.MultiPartStackFile;
import org.soshow.beautydu.http.MultiPartStringRequest;
import org.soshow.beautyedu.json.utils.LogUtils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**  
 * @Package: org.soshow.mingzi.api
 * @Author: chenjiaming
 * @Time: 2015年9月7日 下午5:52:56
 * @File: UploadFileApi.java
 * @Description: 上传文件、图片
 */
public class UploadFileApi {
    private static String TAG = "UploadFileApi";
    private static UploadFileApi uploadFileApi;
    private Context context;
    private RequestQueue singleQueue;
    private RequestQueue singleQueueFile;
    
    private UploadFileApi(){}
    
    private UploadFileApi(Context context){
        this.context = context;
        singleQueue = Volley.newRequestQueue(context, new MultiPartStack());
        singleQueueFile = Volley.newRequestQueue(context, new MultiPartStackFile());
    }
    
    public static UploadFileApi getInstance(Context context) {
        if (uploadFileApi == null) {
            synchronized (UploadFileApi.class) {
                if (uploadFileApi == null) {
                    uploadFileApi = new UploadFileApi(context);
                }
            }

        }
        return uploadFileApi;
    }
    
    /**
     * 
     * @Author: chenjiaming
     * @param url
     * @param files
     * @param params
     * @param resonseListenerString
     * @Description: 上传图片，支持多图上传并做压缩与图片旋转校正
     */
    public void addPutUploadFileRequest(final String url,
            final Map<String, File> files, final Map<String, String> params, Listener<String> resonseListenerString) {
        if (null == url || null == resonseListenerString) {
            return;
        }

        MultiPartStringRequest multiPartRequest = new MultiPartStringRequest(
                Request.Method.POST, url, resonseListenerString, errorListener) {

            @Override
            public Map<String, File> getFileUploads() {
                return files;
            }

            @Override
            public Map<String, String> getStringUploads() {
                return params;
            }
            
        };

        LogUtils.i(TAG, " volley put : image " + url);

        singleQueue.add(multiPartRequest);
    }
    
    /**
     * 
     * @Author: chenjiaming
     * @Time: 2015年9月23日 上午9:10:57
     * @param url
     * @param files
     * @param params
     * @param resonseListenerString
     * @Description: 上传文件，支持文件上传
     */
    public void uploadFileRequest(final String url,
            final Map<String, File> files, final Map<String, String> params, Listener<String> resonseListenerString) {
        if (null == url || null == resonseListenerString) {
            return;
        }

        MultiPartStringRequest multiPartRequest = new MultiPartStringRequest(
                Request.Method.PUT, url, resonseListenerString, errorListener) {

            @Override
            public Map<String, File> getFileUploads() {
                return files;
            }

            @Override
            public Map<String, String> getStringUploads() {
                return params;
            }
            
        };

        LogUtils.i(TAG, " volley put : uploadFile " + url);

        singleQueueFile.add(multiPartRequest);
    }
    
    
    Listener<JSONObject> resonseListener = new Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            LogUtils.i(TAG, " on response json" + response.toString());
        }
    };

    Listener<String> resonseListenerString = new Listener<String>() {

        @Override
        public void onResponse(String response) {
            LogUtils.i(TAG, " on response String" + response.toString());
        }
    };

    ErrorListener errorListener = new ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error != null) {
                if (error.networkResponse != null)
                    LogUtils.i(TAG, " error " + new String(error.networkResponse.data));
            }
        }
    };
}
