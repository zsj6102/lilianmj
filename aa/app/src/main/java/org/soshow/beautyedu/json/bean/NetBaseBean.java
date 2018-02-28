package org.soshow.beautyedu.json.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 网络请求基础Bean
 * 
 * @author wuxioahong
 * @date 2015-7-14 下午2:31:28
 * 
 */
public abstract class NetBaseBean {

	public abstract void initByJson(JSONObject jsonObject) throws JSONException;

}
