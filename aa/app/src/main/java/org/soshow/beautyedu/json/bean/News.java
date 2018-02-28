package org.soshow.beautyedu.json.bean;

import java.io.Serializable;

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
public class News {
//
//	private String article_id;
//	private String article_title;
//	private String article_class;
//	private String article_keyword;
//	private String article_attr;
//	private String content;
//	private String image_id;
//	private String article_add_user;
//	private String article_add_time;
//	private String image_id_list;
//	private String public_time;
    private String add_time;
	private String article_title;
	private String audit_status;
	private String clickcount;
	private String content;
	private String h5_url;
	private int id;
	private int is_show;
	private int public_status;
	private int recordVersion;
    private String photos_url;
	private String create_time;
	private int click_num;

	public String getPhotos_url() {
		return photos_url;
	}

	public void setPhotos_url(String photos_url) {
		this.photos_url = photos_url;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public int getClick_num() {
		return click_num;
	}

	public void setClick_num(int click_num) {
		this.click_num = click_num;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getArticle_title() {
		return article_title;
	}

	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}

	public String getAudit_status() {
		return audit_status;
	}

	public void setAudit_status(String audit_status) {
		this.audit_status = audit_status;
	}

	public String getClickcount() {
		return clickcount;
	}

	public void setClickcount(String clickcount) {
		this.clickcount = clickcount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getH5_url() {
		return h5_url;
	}

	public void setH5_url(String h5_url) {
		this.h5_url = h5_url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIs_show() {
		return is_show;
	}

	public void setIs_show(int is_show) {
		this.is_show = is_show;
	}

	public int getPublic_status() {
		return public_status;
	}

	public void setPublic_status(int public_status) {
		this.public_status = public_status;
	}

	public int getRecordVersion() {
		return recordVersion;
	}

	public void setRecordVersion(int recordVersion) {
		this.recordVersion = recordVersion;
	}
	//	public String getArticle_id() {
//		return article_id;
//	}
//
//	public void setArticle_id(String article_id) {
//		this.article_id = article_id;
//	}
//
//	public String getArticle_title() {
//		return article_title;
//	}
//
//	public void setArticle_title(String article_title) {
//		this.article_title = article_title;
//	}
//
//	public String getContent() {
//		return content;
//	}
//
//	public void setContent(String content) {
//		this.content = content;
//	}
//
//	public String getArticle_class() {
//		return article_class;
//	}
//
//	public void setArticle_class(String article_class) {
//		this.article_class = article_class;
//	}
//
//	public String getArticle_keyword() {
//		return article_keyword;
//	}
//
//	public void setArticle_keyword(String article_keyword) {
//		this.article_keyword = article_keyword;
//	}
//
//	public String getArticle_attr() {
//		return article_attr;
//	}
//
//	public void setArticle_attr(String article_attr) {
//		this.article_attr = article_attr;
//	}
//
//	public String getImage_id() {
//		return image_id;
//	}
//
//	public void setImage_id(String image_id) {
//		this.image_id = image_id;
//	}
//
//	public String getArticle_add_user() {
//		return article_add_user;
//	}
//
//	public void setArticle_add_user(String article_add_user) {
//		this.article_add_user = article_add_user;
//	}
//
//	public String getArticle_add_time() {
//		return article_add_time;
//	}
//
//	public void setArticle_add_time(String article_add_time) {
//		this.article_add_time = article_add_time;
//	}
//
//	public String getImage_id_list() {
//		return image_id_list;
//	}
//
//	public void setImage_id_list(String image_id_list) {
//		this.image_id_list = image_id_list;
//	}
//
//	public String getPublic_time() {
//		return public_time;
//	}
//
//	public void setPublic_time(String public_time) {
//		this.public_time = public_time;
//	}

}
