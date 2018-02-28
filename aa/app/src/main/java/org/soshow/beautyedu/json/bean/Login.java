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
public class Login extends NetBaseBean {

	public String result;
	public String username;
	// public String info;
	public String email;
	public String photo;
	public String cell_phone;
	public String user_id;
	public String signature;
	public String income;//余额

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.result = jsonObject.optString("result");
		this.username = jsonObject.optString("username");
		// this.info = jsonObject.optString("info");
		this.email = jsonObject.optString("email");
		this.cell_phone = jsonObject.optString("cell_phone");
		this.photo = jsonObject.optString("photo");
		this.user_id = jsonObject.optString("user_id");
		this.signature = jsonObject.optString("signature");
		this.income = jsonObject.optString("income");
	}

}
