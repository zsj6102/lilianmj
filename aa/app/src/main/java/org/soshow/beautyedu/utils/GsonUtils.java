/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautyedu.utils;

import android.support.annotation.NonNull;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @Author: huangenqiang
 * @Time: 2015年8月19日 下午6:09:09
 * @Description: Gson解析工具类
 */
public final class GsonUtils {

    public static <T> T parseJSON(String json, Class<T> clazz) {
        // Gson gson = new Gson();
        // T info = gson.fromJson(json, clazz);
        Gson gson = builder();
        T info = gson.fromJson(json, clazz);
        return info;
    }

    @NonNull
    private static Gson builder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.serializeNulls().create();
    }

    /**
     * Type type = new TypeToken&lt;ArrayList&lt;TypeInfo>>(){}.getType();
     * Type所在的包：java.lang.reflect
     * TypeToken所在的包：com.google.gson.reflect.TypeToken
     * @param json
     * @param type
     * @return
     */
    public static <T> T parseJSONArray(String jsonArr, Type type) {
        Gson gson = builder();
        T infos = gson.fromJson(jsonArr, type);
        return infos;
    }

    private GsonUtils() {
    }
}
