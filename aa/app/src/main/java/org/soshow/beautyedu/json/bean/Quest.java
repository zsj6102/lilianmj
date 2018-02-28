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
public class Quest extends NetBaseBean {
	public JSONObject jo;
	public String quest_id;
	public String quest_type;
	public String quest_answer;
	public String quest_title;
	public String quest_select_num;
	// public String quest_select_1;
	// public String quest_select_2;
	// public String quest_select_3;
	// public String quest_select_4;
	// public String quest_select_5;
	// public String quest_select_6;
	public String quest_analyse;
	public String[] choice;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		jo = jsonObject.optJSONObject("info");
		initByJsonAgain(jo);
	}

	private void initByJsonAgain(JSONObject jsonObject) throws JSONException {
		this.quest_id = jsonObject.optString("quest_id");
		this.quest_type = jsonObject.optString("quest_type");
		this.quest_answer = jsonObject.optString("quest_answer");
		this.quest_title = jsonObject.optString("quest_title");
		this.quest_select_num = jsonObject.optString("quest_select_num");
		choice = new String[Integer.parseInt(quest_select_num)];
		for (int i = 1; i <= Integer.parseInt(quest_select_num); i++) {
			choice[i - 1] = jsonObject.optString("quest_select_" + i);
		}

		// this.quest_select_1 = jsonObject.optString("quest_select_1");
		// this.quest_select_2 = jsonObject.optString("quest_select_2");
		// this.quest_select_3 = jsonObject.optString("quest_select_3");
		// this.quest_select_4 = jsonObject.optString("quest_select_4");
		// this.quest_select_5 = jsonObject.optString("quest_select_5");
		// this.quest_select_6 = jsonObject.optString("quest_select_6");
		// this.quest_analyse = jsonObject.optString("quest_analyse");
	}
}
