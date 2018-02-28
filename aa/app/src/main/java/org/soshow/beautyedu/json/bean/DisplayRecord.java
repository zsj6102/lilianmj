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
public class DisplayRecord extends NetBaseBean {
	public JSONObject lecture_info;
	//public static String lecture_info_string;
	public String lecture_id;
	public String lecture_title;
	public String lecture_content;
	public String lecture_cat1;
	public String lecture_cat2;
	public String lecture_cat3;
	public String lecture_cat4;
	public String lecture_cat5;
	public String lecture_pics_id;
	public String lecture_questions_id;
	public String lecture_add_user;
	public String lecture_add_time;
	public String lecture_click_count;
	public String video_cloud_id;
	public String front_display_pic;
	public String head_image;
    public long add_time;
	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		// lecture_info_string = jsonObject.optString("lecture_info");
		lecture_info = jsonObject.optJSONObject("lecture_info");
        add_time=jsonObject.optLong("add_time");
		initByJsonAgain(lecture_info);
		// this.lecture_id = jsonObject.optString("lecture_id");
		// this.lecture_title = jsonObject.optString("lecture_title");
		// this.lecture_content = jsonObject.optString("lecture_content");
		// this.lecture_cat1 = jsonObject.optString("lecture_cat1");
		// this.lecture_cat2 = jsonObject.optString("lecture_cat2");
		// this.lecture_cat3 = jsonObject.optString("lecture_cat3");
		// this.lecture_cat4 = jsonObject.optString("lecture_cat4");
		// this.lecture_cat5 = jsonObject.optString("lecture_cat5");
		// this.lecture_pics_id = jsonObject.optString("lecture_pics_id");
		// this.lecture_questions_id =
		// jsonObject.optString("lecture_questions_id");
		// this.lecture_add_user = jsonObject.optString("lecture_add_user");
		// this.lecture_add_time = jsonObject.optString("lecture_add_time");
		// this.lecture_click_count =
		// jsonObject.optString("lecture_click_count");
		// this.video_cloud_id = jsonObject.optString("video_cloud_id");
		// this.front_display_pic = jsonObject.optString("front_display_pic");
		// this.image_url = jsonObject.optString("image_url");
		// Log.d("345abc", "解析的url= " + this.image_url);

	}

	public void initByJsonAgain(JSONObject jsonObject) throws JSONException {
		// lecture_info= jsonObject.optJSONObject("info");
		this.lecture_id = jsonObject.optString("lecture_id");
		this.lecture_title = jsonObject.optString("lecture_title");
		// this.lecture_content = jsonObject.optString("lecture_content");
		// this.lecture_cat1 = jsonObject.optString("lecture_cat1");
		// this.lecture_cat2 = jsonObject.optString("lecture_cat2");
		// this.lecture_cat3 = jsonObject.optString("lecture_cat3");
		// this.lecture_cat4 = jsonObject.optString("lecture_cat4");
		// this.lecture_cat5 = jsonObject.optString("lecture_cat5");
		// this.lecture_pics_id = jsonObject.optString("lecture_pics_id");
		// this.lecture_questions_id =
		// jsonObject.optString("lecture_questions_id");
		// this.lecture_add_user = jsonObject.optString("lecture_add_user");
		this.lecture_add_time = jsonObject.optString("lecture_add_time");
		this.lecture_click_count = jsonObject.optString("lecture_click_count");
		// this.video_cloud_id = jsonObject.optString("video_cloud_id");
		// this.front_display_pic = jsonObject.optString("front_display_pic");
		this.head_image = jsonObject.optString("head_image");
		

	}
}
