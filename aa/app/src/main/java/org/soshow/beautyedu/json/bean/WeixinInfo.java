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
public class WeixinInfo extends NetBaseBean {

	public String nickname;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.nickname = jsonObject.optString("nickname");

	}

}
