package org.soshow.beautyedu.json.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
public class LunBo extends NetBaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public String result;
	public String src;
	public String image_url;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.src = jsonObject.optString("src");
		this.image_url = jsonObject.optString("image_url");
	}
}
