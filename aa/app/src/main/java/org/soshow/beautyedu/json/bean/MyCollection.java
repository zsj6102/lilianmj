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
public class MyCollection extends NetBaseBean {

	public String bookmark_id;
	public String user_id;
	public String gender;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		//this.id = jsonObject.optString("id");
		//this.name = jsonObject.optString("name");
		this.gender = jsonObject.optString("gender");
	}

}
