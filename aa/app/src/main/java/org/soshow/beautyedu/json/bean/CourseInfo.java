package org.soshow.beautyedu.json.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import com.android.volley.toolbox.JsonArrayRequest;

/**
 * 
 * 用户实体类
 * 
 * @author wuxioahong
 * @date 2015-7-21 下午4:05:07
 * 
 */
public class CourseInfo extends NetBaseBean {

	public String my_course_id;
	public String course_pack_id;
	public String duration;
	public String video_static;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.course_pack_id = jsonObject.optString("course_pack_id");
		this.my_course_id = jsonObject.optString("my_course_id");
		this.duration = jsonObject.optString("duration");
		this.video_static = jsonObject.optString("video_static");

	}

}
