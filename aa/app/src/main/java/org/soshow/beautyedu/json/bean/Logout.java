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
public class Logout extends NetBaseBean {

	public String result;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.result = jsonObject.optString("result");

	}

}
