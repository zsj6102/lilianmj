package org.soshow.beautyedu.json.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MyCourseInfo extends NetBaseBean{


    public String course_picture;
    public String order_state;
    public String course_price;
    public String course_credit_hours;
    public String course_version;
    public String learn_count;
    public String course_cat1_id;
    public String course_icon;
    public String addtime;
    public String course_intro;
    public String course_title;
    public String order_num;
    public String course_duration_count;
    public String user_id;
    public String course_description;
    public String course_id;
    public String course_cat2_id;
    public String course_type;
    public String course_order_id;
	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.course_picture = jsonObject.optString("course_picture");
		this.order_state = jsonObject.optString("order_state");
		this.course_price = jsonObject.optString("course_price");
		this.course_credit_hours = jsonObject.optString("course_credit_hours");
		this.course_version = jsonObject.optString("course_version");
		this.learn_count = jsonObject.optString("learn_count");
		this.course_cat1_id = jsonObject.optString("course_cat1_id");
		this.course_icon = jsonObject.optString("course_icon");
		this.addtime = jsonObject.optString("addtime");
		this.course_intro = jsonObject.optString("course_intro");
		this.course_title = jsonObject.optString("course_title");
		this.order_num = jsonObject.optString("order_num");
		this.course_duration_count = jsonObject.optString("course_duration_count");
		this.user_id = jsonObject.optString("user_id");
		this.course_description = jsonObject.optString("course_description");
		this.course_id = jsonObject.optString("course_id");
		this.course_cat2_id = jsonObject.optString("course_cat2_id");
		this.course_type = jsonObject.optString("course_type");
		this.course_order_id = jsonObject.optString("course_order_id");
	}

}
