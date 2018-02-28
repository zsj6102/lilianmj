package org.soshow.beautyedu.json.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 
 * 用户实体类
 * 
 * @author wuxioahong
 * @date 2015-7-21 下午4:05:07
 * 
 */
public class LectureList extends NetBaseBean {
	// public JSONObject lecture_info;
	// public static String lecture_info_string;
	public String lecture_id;
	public String lecture_title;
	public String lecture_content_url;
	public String lecture_cat1;
	public String lecture_cat3;
	public String head_image;
	public String lecture_click_count;
	public String video_status;
	public String video_local_name;
	public String lecture_questions_id;
	public String add_time;
	//public String total;

	public JSONArray play_urls;
	public String url;
	public String url_o;
	public String url_p;
	public String url_s;
	public String url_h;
	public int definition;
	public int definition_o;
	public int definition_p;
	public int definition_s;
	public int definition_h;
	public int vbitrate;
	public int vbitrate_o;
	public int vbitrate_p;
	public int vbitrate_s;
	public int vbitrate_h;
	public int vheight;
	public int vheight_o;
	public int vheight_p;
	public int vheight_s;
	public int vheight_h;
	public int vwidth;
	public int vwidth_o;
	public int vwidth_p;
	public int vwidth_s;
	public int vwidth_h;
	public String price;
	public String lecture_operate;
	public String remark;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {

		play_urls = jsonObject.optJSONArray("play_urls");

		lecture_id = jsonObject.optString("lecture_id");
		price = jsonObject.optString("price");
		lecture_operate = jsonObject.optString("lecture_operate");
		lecture_title = jsonObject.optString("lecture_title");
		lecture_content_url = jsonObject.optString("lecture_content_url");
		lecture_cat1 = jsonObject.optString("lecture_cat1");
		lecture_cat3 = jsonObject.optString("lecture_cat3");
		head_image = jsonObject.optString("head_image");
		lecture_click_count = jsonObject.optString("lecture_click_count");
		video_status = jsonObject.optString("video_status");
		video_local_name = jsonObject.optString("video_local_name");
		remark = jsonObject.optString("remark");
	//	total = jsonObject.optString("total");
		if (remark == null || remark.equals("")) {
			remark = "2";
		}
		// Log.d("abccba", "remark为" + remark);
		lecture_questions_id = jsonObject.optString("lecture_questions_id");
		add_time = jsonObject.optString("add_time");
		// Log.d("1221", "add_time为 "+add_time);
		if (play_urls != null) {
			for (int i = 0; i < play_urls.length(); i++) {
				try {
					JSONObject object = play_urls.getJSONObject(i);
					// Log.d("123456", "得到的JSONObject为" + object.toString());
					try {
						initByJsonAgain(object);
					} catch (Exception e) {
						// TODO: handle exception
					Log.d("1221", "再次解析错误" + e.toString());
					}
				} catch (Exception e) {
					Log.d("1221", "得到json错误" + e.toString());
				}

			}
		}
	}

	public void initByJsonAgain(JSONObject jsonObject) throws JSONException {
		// lecture_info= jsonObject.optJSONObject("info");
		definition = jsonObject.optInt("definition");

		// Log.d("123456", "解析的definition为" + definition);
		switch (definition) {
		case 0:
			url_o = jsonObject.optString("url");
			definition_o = jsonObject.optInt("definition");
			vbitrate_o = jsonObject.optInt("vbitrate");
			definition_o = jsonObject.optInt("definition");
			vheight_o = jsonObject.optInt("vheight");
			vwidth_o = jsonObject.optInt("vwidth");
			break;
		case 1:
			url_o = jsonObject.optString("url");
			definition_o = jsonObject.optInt("definition");
			vbitrate_o = jsonObject.optInt("vbitrate");
			definition_o = jsonObject.optInt("definition");
			vheight_o = jsonObject.optInt("vheight");
			vwidth_o = jsonObject.optInt("vwidth");
			break;
		case 10:
			url_p = jsonObject.optString("url");
			definition_p = jsonObject.optInt("definition");
			vbitrate_p = jsonObject.optInt("vbitrate");
			definition_p = jsonObject.optInt("definition");
			vheight_p = jsonObject.optInt("vheight");
			vwidth_p = jsonObject.optInt("vwidth");
			break;
		case 20:
			url_s = jsonObject.optString("url");
			definition_s = jsonObject.optInt("definition");
			vbitrate_s = jsonObject.optInt("vbitrate");
			definition_s = jsonObject.optInt("definition");
			vheight_s = jsonObject.optInt("vheight");
			vwidth_s = jsonObject.optInt("vwidth");
			break;
		case 30:
			url_h = jsonObject.optString("url");
			definition_h = jsonObject.optInt("definition");
			vbitrate_h = jsonObject.optInt("vbitrate");
			definition_h = jsonObject.optInt("definition");
			vheight_h = jsonObject.optInt("vheight");
			vwidth_h = jsonObject.optInt("vwidth");
			break;
		case 110:
			url_p = jsonObject.optString("url");
			definition_p = jsonObject.optInt("definition");
			vbitrate_p = jsonObject.optInt("vbitrate");
			definition_p = jsonObject.optInt("definition");
			vheight_p = jsonObject.optInt("vheight");
			vwidth_p = jsonObject.optInt("vwidth");
			break;
		case 120:
			url_s = jsonObject.optString("url");
			definition_s = jsonObject.optInt("definition");
			vbitrate_s = jsonObject.optInt("vbitrate");
			definition_s = jsonObject.optInt("definition");
			vheight_s = jsonObject.optInt("vheight");
			vwidth_s = jsonObject.optInt("vwidth");
			break;
		case 130:
			url_h = jsonObject.optString("url");
			definition_h = jsonObject.optInt("definition");
			vbitrate_h = jsonObject.optInt("vbitrate");
			definition_h = jsonObject.optInt("definition");
			vheight_h = jsonObject.optInt("vheight");
			vwidth_h = jsonObject.optInt("vwidth");
			break;
		default:
			break;
		}
	}
}
