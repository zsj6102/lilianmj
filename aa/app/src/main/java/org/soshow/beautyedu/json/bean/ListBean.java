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
public class ListBean extends NetBaseBean {

	public String id;
	public String hdf_yy_name;
	public String hdf_yy_tese;
	public String hdf_yy_dizhi;
	public String picname_hospital_s;

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.id = jsonObject.optString("id");
		this.hdf_yy_name = jsonObject.optString("hdf_yy_name");
		this.hdf_yy_tese = jsonObject.optString("hdf_yy_tese");
		this.hdf_yy_dizhi = jsonObject.optString("hdf_yy_dizhi");
		this.picname_hospital_s = jsonObject.optString("picname_hospital_s");

	}

}
