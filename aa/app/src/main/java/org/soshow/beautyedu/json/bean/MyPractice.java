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
public class MyPractice extends NetBaseBean {

	public String paper_id;
	public String user_id;
	public String cat1_id;
	public String cat2_id;
	public String add_time;
	public String finish_time;
	public String last_exercise_time;
	public String current_quest_id;
	public String cat1_name;
	public String cat2_name;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.paper_id = jsonObject.optString("paper_id");
		this.user_id = jsonObject.optString("user_id");
		this.cat1_id = jsonObject.optString("cat1_id");
		this.cat2_id = jsonObject.optString("cat2_id");
		this.add_time = jsonObject.optString("add_time");
		this.last_exercise_time = jsonObject.optString("last_exercise_time");
		this.current_quest_id = jsonObject.optString("current_quest_id");
		this.cat1_name = jsonObject.optString("cat1_name");
		this.cat2_name = jsonObject.optString("cat2_name");
		this.finish_time= jsonObject.optString("finish_time");
	}

}
