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
public class TitleCollect extends NetBaseBean {

	public String bookmark_id;
	public String user_id;
	public String quest_id;
	public String add_time;
	public String quest_title;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.bookmark_id = jsonObject.optString("bookmark_id");
		this.user_id = jsonObject.optString("user_id");
		this.quest_id = jsonObject.optString("quest_id");
		this.add_time = jsonObject.optString("add_time");
		this.quest_title = jsonObject.optString("quest_title");
	}

}
