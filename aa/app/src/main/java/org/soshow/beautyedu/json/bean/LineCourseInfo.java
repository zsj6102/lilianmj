package org.soshow.beautyedu.json.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class LineCourseInfo extends NetBaseBean {
	
	public String offline_id;
	public String title;
	public String addtime;
	public String price;
	public String content;
	public String start_time;
	public String end_time;
	public String state;
	public String img_url;
	public String describe;
	public String publish;
	public String content_url;
	public String describe_url;
	public String max_person;
	public String is_sign;
	public String order_state;
	public boolean is_vip;
	public String name;//所属学校
	public String address;//地址
	

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.offline_id = jsonObject.optString("offline_id");
		this.title = jsonObject.optString("title");
		this.addtime = jsonObject.optString("addtime");
		this.price = jsonObject.optString("price");
		this.content = jsonObject.optString("content");
		this.start_time = jsonObject.optString("start_time");
		this.end_time = jsonObject.optString("end_time");
		this.state = jsonObject.optString("state");
		this.img_url = jsonObject.optString("img_url");
		this.describe = jsonObject.optString("describe");
		this.publish = jsonObject.optString("publish");
		this.max_person = jsonObject.optString("max_person");
		this.is_sign = jsonObject.optString("is_sign");
		this.order_state = jsonObject.optString("order_state");
		this.is_vip = jsonObject.optBoolean("is_vip");
		this.name = jsonObject.optString("name");
		this.address = jsonObject.optString("address");
		this.content_url = jsonObject.optString("content_url");
		this.describe_url = jsonObject.optString("describe_url");
	}


}
