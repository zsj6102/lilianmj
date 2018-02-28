package org.soshow.beautydu.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpClientStack.HttpPatch;
import com.android.volley.toolbox.HurlStack;

/**
 * 
 * @Author: chenjiaming
 * @Time: 2015年9月7日 下午4:12:17
 * @Description: 这个Stack用于上传文件, 如果没有这个Stack, 则上传文件不成功
 */
public class MultiPartStack extends HurlStack {
    @SuppressWarnings("unused")
    private static final String TAG = MultiPartStack.class.getSimpleName();
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final int compressImage = 500;

    @Override
    public HttpResponse performRequest(Request<?> request,
            Map<String, String> additionalHeaders) throws IOException,
            AuthFailureError {

        if (!(request instanceof MultiPartRequest)) {
            return super.performRequest(request, additionalHeaders);
        } else {
            return performMultiPartRequest(request, additionalHeaders);
        }
    }

    private static void addHeaders(HttpUriRequest httpRequest,
            Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    public HttpResponse performMultiPartRequest(Request<?> request,
            Map<String, String> additionalHeaders) throws IOException,
            AuthFailureError {
        HttpUriRequest httpRequest = createMultiPartRequest(request,
                additionalHeaders);
        addHeaders(httpRequest, additionalHeaders);
        addHeaders(httpRequest, request.getHeaders());
        HttpParams httpParams = httpRequest.getParams();
        // int timeoutMs = request.getTimeoutMs();
        int timeoutMs = 360000;

        if (timeoutMs != -1) {
            HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
        }

        /* Make a thread safe connection manager for the client */
        HttpClient httpClient = new DefaultHttpClient(httpParams);

        return httpClient.execute(httpRequest);
    }

    static HttpUriRequest createMultiPartRequest(Request<?> request,
            Map<String, String> additionalHeaders) throws AuthFailureError {
        switch (request.getMethod()) {
        case Method.DEPRECATED_GET_OR_POST: {
            // This is the deprecated way that needs to be handled for backwards
            // compatibility.
            // If the request's post body is null, then the assumption is that
            // the request is
            // GET. Otherwise, it is assumed that the request is a POST.
            byte[] postBody = request.getBody();
            if (postBody != null) {
                HttpPost postRequest = new HttpPost(request.getUrl());
                if (request.getBodyContentType() != null)
                    postRequest.addHeader(HEADER_CONTENT_TYPE,
                            request.getBodyContentType());
                HttpEntity entity;
                entity = new ByteArrayEntity(postBody);
                postRequest.setEntity(entity);
                return postRequest;
            } else {
                return new HttpGet(request.getUrl());
            }
        }
        case Method.GET:
            return new HttpGet(request.getUrl());
        case Method.DELETE:
            return new HttpDelete(request.getUrl());
        case Method.POST: {
            HttpPost postRequest = new HttpPost(request.getUrl());
            if (request.getBodyContentType() != null) {
                postRequest.addHeader(HEADER_CONTENT_TYPE,
                        request.getBodyContentType());
            }
            setMultiPartBody(postRequest, request);
            return postRequest;
        }
        case Method.PUT: {
            HttpPut putRequest = new HttpPut(request.getUrl());
            if (request.getBodyContentType() != null)
                putRequest.addHeader(HEADER_CONTENT_TYPE,
                        request.getBodyContentType());
            setMultiPartBody(putRequest, request);
            return putRequest;
        }
        // Added in source code of Volley libray.
        case Method.PATCH: {
            HttpPatch patchRequest = new HttpPatch(request.getUrl());
            if (request.getBodyContentType() != null)
                patchRequest.addHeader(HEADER_CONTENT_TYPE,
                        request.getBodyContentType());
            return patchRequest;
        }
        default:
            throw new IllegalStateException("Unknown request method.");
        }
    }

    /**
     * If Request is MultiPartRequest type, then set MultipartEntity in the
     * httpRequest object.
     * 
     * @param httpRequest
     * @param request
     * @throws AuthFailureError
     */
    @SuppressWarnings("deprecation")
    private static void setMultiPartBody(
            HttpEntityEnclosingRequestBase httpRequest, Request<?> request)
            throws AuthFailureError {

        // Return if Request is not MultiPartRequest
        if (!(request instanceof MultiPartRequest)) {
            return;
        }

        // MultipartEntity multipartEntity = new
        // MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        /* example for setting a HttpMultipartMode */
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        // Iterate the stringUploads
        Map<String, String> stringUpload = ((MultiPartRequest) request)
                .getStringUploads();
        for (Map.Entry<String, String> entry : stringUpload.entrySet()) {
            try {
                builder.addPart(((String) entry.getKey()), new StringBody(
                        (String) entry.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Iterate the fileUploads
        Map<String, File> fileUpload = ((MultiPartRequest) request)
                .getFileUploads();
        // for (Map.Entry<String, File> entry : fileUpload.entrySet()) {
        //
        // builder.addPart(((String) entry.getKey()), new FileBody((File)
        // entry.getValue()));
        // }

        for (Map.Entry<String, File> entry : fileUpload.entrySet()) {

            if (!entry.getValue().exists()) {
                continue;
            }

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue()
                    .getAbsolutePath().replace("file://", ""), null);

            // 旋转判断
            int degree = ImageUtil.getBitmapDegree(entry.getValue()
                    .getAbsolutePath().replace("file://", ""));
            if (degree != 0) {
                bitmap = ImageUtil.getRotateBitmap(bitmap, degree);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            while (baos.toByteArray().length / 1024 > compressImage) {
                baos.reset();
                options -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            builder.addPart(
                    entry.getKey(),
                    new ByteArrayBody(baos.toByteArray(), String.valueOf(System
                            .currentTimeMillis())));
        }

        httpRequest.setEntity(builder.build());
    }

}
