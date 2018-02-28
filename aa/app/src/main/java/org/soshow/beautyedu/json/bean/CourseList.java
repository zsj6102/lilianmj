package org.soshow.beautyedu.json.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import com.android.volley.toolbox.JsonArrayRequest;

import android.R.string;

/**
 * 
 * 用户实体类
 * 
 * @author wuxioahong
 * @date 2015-7-21 下午4:05:07
 * 
 */
public class CourseList extends NetBaseBean {

	public String course_pack_id;
	public String course_id;
	public String course_pack_type;
	public String lecture_id;
	public String paper_id;
	public String weight;
	public String title;
	public String learn_status;
	public String my_duration;
	public String play_duration;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.course_pack_id = jsonObject.optString("course_pack_id");
		this.course_id = jsonObject.optString("course_id");
		this.course_pack_type = jsonObject.optString("course_pack_type");
		this.lecture_id = jsonObject.optString("lecture_id");
		this.paper_id = jsonObject.optString("paper_id");
		this.weight = jsonObject.optString("weight");
		this.title = jsonObject.optString("title");
		this.learn_status = jsonObject.optString("learn_status");
		this.my_duration = jsonObject.optString("my_duration");
		this.play_duration = jsonObject.optString("play_duration");

	}

}
