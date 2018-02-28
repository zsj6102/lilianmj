package org.soshow.beautyedu.json.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 用户实体类
 * 
 * @author wuxioahong
 * @date 2015-7-21 下午4:05:07
 * 
 */
public class Weixin extends NetBaseBean {

	public String access_token;
	public String refresh_token;
	public String openid;
	public String scope;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.access_token = jsonObject.optString("access_token");
		this.refresh_token = jsonObject.optString("refresh_token");
		this.openid = jsonObject.optString("openid");
		this.scope = jsonObject.optString("scope");

	}

}
