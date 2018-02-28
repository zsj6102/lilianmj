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
public class VideoCollection extends NetBaseBean {

	public String bookmark_id;
	public String user_id;
	public String add_time;
	public String lecture_title;
	public String lecture_id;
	public String lecture_click_count;
	public String head_image;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.bookmark_id = jsonObject.optString("bookmark_id");
		this.user_id = jsonObject.optString("user_id");
		this.add_time = jsonObject.optString("add_time");
		this.lecture_title = jsonObject.optString("lecture_title");
		this.lecture_id = jsonObject.optString("lecture_id");
		this.lecture_click_count = jsonObject.optString("lecture_click_count");
		this.head_image = jsonObject.optString("head_image");
	}

}
