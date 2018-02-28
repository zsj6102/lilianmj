package org.soshow.beautyedu.json.bean;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.utils.StringUtil;

import android.util.Log;

/**
 * 
 * 用户实体类
 * 
 * @author wuxiaohong
 * @date 2015-7-21 下午4:05:07
 * 
 */
public class NewsDetails extends NetBaseBean {

	public String article_id;
	public String article_title;
	public String article_class;
	public String article_keyword;
	public String article_attr;
	public String content;
	public String image_id;
	public String image_id_list;
	public String article_add_time;
	public String result;
	public String info;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.result = jsonObject.optString("result");
		// Log.d("345abc", "返回的升级result= " + result);
		if (result.equals("0")) {
			JSONObject jo = jsonObject.optJSONObject("info");
			initByJsonAgain(jo);
		} else {
			info = jsonObject.optString("info");
		}

	}

	public void initByJsonAgain(JSONObject jsonObject) throws JSONException {

		this.article_id = jsonObject.optString("article_id");
		// Log.d("345abc", "返回的升级版本名称为= " + versionName);
		this.article_title = jsonObject.optString("article_title");

		this.article_class = jsonObject.optString("article_class");
		this.article_keyword = jsonObject.optString("article_keyword");

		this.article_attr = jsonObject.optString("article_attr");
		this.content = jsonObject.optString("content");
		this.image_id = jsonObject.optString("image_id");
		this.image_id_list = jsonObject.optString("image_id_list");
		this.article_add_time = jsonObject.optString("article_add_time");

	}
}
