package org.soshow.beautyedu.bean;

public class CommentInfo {

	private String comment_id;
	private String content;
	private String create_time;
	private String create_time_pre;

	private Integer id;

	private String user_id;
	private String user_nickname;
	private String user_photo_url;


	private String replyto_user_nickname;

	//del
	private String lecture_id;
	private String cell_phone;
	private String real_name;
	private String gender;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	//vo  start
	public String getUsername() {return getUser_nickname();};


	//gett sett
	public String getCreate_time_pre() {
		return create_time_pre;
	}

	public void setCreate_time_pre(String create_time_pre) {
		this.create_time_pre = create_time_pre;
	}

	public String getReplyto_user_nickname() {
		return replyto_user_nickname;
	}

	public void setReplyto_user_nickname(String replyto_user_nickname) {
		this.replyto_user_nickname = replyto_user_nickname;
	}

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLecture_id() {
		return lecture_id;
	}

	public void setLecture_id(String lecture_id) {
		this.lecture_id = lecture_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_nickname() {
		return user_nickname;
	}

	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}

	public String getCell_phone() {
		return cell_phone;
	}

	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getUser_photo_url() {
		return user_photo_url;
	}

	public void setUser_photo_url(String user_photo_url) {
		this.user_photo_url = user_photo_url;
	}

}
