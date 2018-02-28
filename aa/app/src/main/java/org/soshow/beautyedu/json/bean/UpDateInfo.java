package org.soshow.beautyedu.json.bean;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.StringUtil;

import android.text.style.UpdateAppearance;
import android.util.Log;

/** app更新信息 */
public class UpDateInfo extends  JsonResult {
	 
	public String		detail;            // "更新明细",
	public String		device_type;            // "android",             //android ios
	public String		force_update;            // 0,                    //是否强制升级 1是,0 否
	public Integer		size;           // kb,
	public String 		md5;
	public String		url;            // ;http            //            //xxxxxxx/1.apk",            //升级地址      .apk .ipa 地址 or 升级h5页地址
	public String		version;            // "1.0.1",                   //
	public Integer		version_code;            // 10001                 //版本号 , 判断是否需要升级

	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		super.initByJson(jsonObject);
		try {
			if(isSuccess() && !StringUtil.isEmpty(this.info)){
				UpDateInfo obj = GsonUtils.parseJSON(this.info, UpDateInfo.class);

				this.detail=obj.detail;
				this.device_type=obj.device_type;
				this.force_update=obj.force_update;
				this.md5=obj.md5;
				this.size=obj.size;
				this.url=obj.url;
				this.version=obj.version;
				this.version_code=obj.version_code;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
