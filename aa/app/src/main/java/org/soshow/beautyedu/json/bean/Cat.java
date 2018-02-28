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
public class Cat extends NetBaseBean {

	public String exam_cat_id;
	public String exam_cat_name;
	public String exam_cat_pid;
	public String exam_cat_lvl;
	public String is_use;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.exam_cat_id = jsonObject.optString("exam_cat_id");
		this.exam_cat_name = jsonObject.optString("exam_cat_name");
		this.exam_cat_pid = jsonObject.optString("exam_cat_pid");
		this.exam_cat_lvl = jsonObject.optString("exam_cat_lvl");
		this.is_use = jsonObject.optString("is_use");
	}

}
