package org.soshow.beautyedu.bean;
import org.soshow.beautyedu.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonInfo {
	/**
	 * 邀请码
	 */
	private String invitation_code;

	public Integer id;
	/** 帐号 */
	public String username;
	/** 密码 */
	public String password;
	/** 手机号 */
	public String mobile;
	/** 昵称 */
	public String nickname;
	/** 邮箱 */
	public String email;
	/** 状态(0:禁用,1:启用,2:) */
	public String status;
	/** 性别(0:保密,1:女,2:男) */
	public String gender;
	/** 生日(yyyy-mm) */
	public String birthday;
	/** 头像 */
	public String photo_url;
	/** 签名 */
	public String signature;
	public String real_name;
	public String create_user_id;
	/** 上次登录时间 */
	public String last_login_time;
	/** 登录次数 */
	public Integer login_count;
	//
	public String huanxin;
	public String huanxinpwd;
	/**相册三张  ; 分割*/
	public String album_photos_url;

	public String wxopenid_app;
    public String area;
	public String integral;

	public String getInvitation_code() {
		return invitation_code;
	}

	public void setInvitation_code(String invitation_code) {
		this.invitation_code = invitation_code;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getWxopenid_app() {
		return wxopenid_app;
	}

	public void setWxopenid_app(String wxopenid_app) {
		this.wxopenid_app = wxopenid_app;
	}

	//VO
	public List getAlbum_photos_url_list(){
		String s=getAlbum_photos_url();
		List photos = new ArrayList();
		if(!StringUtil.isEmpty(s)){
			String[] arr = s.split(";");
			if(arr!=null){
				photos.addAll(Arrays.asList(arr));
			}
		}
		return  photos;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	//gett sett
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getCreate_user_id() {
		return create_user_id;
	}

	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}

	public String getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(String last_login_time) {
		this.last_login_time = last_login_time;
	}

	public Integer getLogin_count() {
		return login_count;
	}

	public void setLogin_count(Integer login_count) {
		this.login_count = login_count;
	}

	public String getHuanxin() {
		return huanxin;
	}

	public void setHuanxin(String huanxin) {
		this.huanxin = huanxin;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getHuanxinpwd() {
		return huanxinpwd;
	}

	public void setHuanxinpwd(String huanxinpwd) {
		this.huanxinpwd = huanxinpwd;
	}

	public String getAlbum_photos_url() {
		return album_photos_url;
	}

	public void setAlbum_photos_url(String album_photos_url) {
		this.album_photos_url = album_photos_url;
	}
}
