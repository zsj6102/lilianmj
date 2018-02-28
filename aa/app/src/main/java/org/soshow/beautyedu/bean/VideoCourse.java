package org.soshow.beautyedu.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12.
 */
public class VideoCourse implements Serializable{

	/**
	 * lecture_id : 180 lecture_title : 烫染课程 lecture_add_time : 2016-06-06
	 * 17:12:02 lecture_click_count : 0 video_local_name :
	 * http://192.168.1.112:8088
	 * /upload/videos/1259479724_xcjlajlrthbgtzkxxnnx.mp4 image_url : head_image
	 * : http://192.168.1.112:8088/upload/video_pic/s_aa360b20160606.jpg price :
	 * 0.00 lecture_keyword : 烫染课程 category_id : 6 chapter_id : 1 lecture_type :
	 * 1 lecture_link : lecture_order_id : 0 order_num : 0 user_id : 0
	 * order_state : 0 is_vip : false lecture_graphic_content_url :
	 */

	private String lecture_id;
	private String lecture_title;
	private String lecture_content;
	private String lecture_add_time;
	private String lecture_click_count;
	private String video_local_name;
	private String image_url;
	private String head_image;
	private String price;
	private String lecture_keyword;
	private String category_id;
	private String chapter_id;
	private String lecture_type;
	private String lecture_link;
	private int lecture_order_id;
	private String order_num;
	private int user_id;
	private String order_state;
	private boolean is_vip;
	private String lecture_graphic_content_url;
	private String school_id;
	private String lecture_content_url;
	private String lecture_integral;
	
	

	public String getLecture_integral() {
		return lecture_integral;
	}

	public void setLecture_integral(String lecture_integral) {
		this.lecture_integral = lecture_integral;
	}

	public String getLecture_content_url() {
		return lecture_content_url;
	}

	public void setLecture_content_url(String lecture_content_url) {
		this.lecture_content_url = lecture_content_url;
	}

	public String getLecture_content() {
		return lecture_content;
	}

	public void setLecture_content(String lecture_content) {
		this.lecture_content = lecture_content;
	}

	public void setLecture_id(String lecture_id) {
		this.lecture_id = lecture_id;
	}

	public void setLecture_title(String lecture_title) {
		this.lecture_title = lecture_title;
	}

	public void setLecture_add_time(String lecture_add_time) {
		this.lecture_add_time = lecture_add_time;
	}

	public void setLecture_click_count(String lecture_click_count) {
		this.lecture_click_count = lecture_click_count;
	}

	public void setVideo_local_name(String video_local_name) {
		this.video_local_name = video_local_name;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public void setHead_image(String head_image) {
		this.head_image = head_image;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setLecture_keyword(String lecture_keyword) {
		this.lecture_keyword = lecture_keyword;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public void setChapter_id(String chapter_id) {
		this.chapter_id = chapter_id;
	}

	public void setLecture_type(String lecture_type) {
		this.lecture_type = lecture_type;
	}

	public void setLecture_link(String lecture_link) {
		this.lecture_link = lecture_link;
	}

	public void setLecture_order_id(int lecture_order_id) {
		this.lecture_order_id = lecture_order_id;
	}

	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}

	public void setIs_vip(boolean is_vip) {
		this.is_vip = is_vip;
	}

	public void setLecture_graphic_content_url(
			String lecture_graphic_content_url) {
		this.lecture_graphic_content_url = lecture_graphic_content_url;
	}

	public String getLecture_id() {
		return lecture_id;
	}

	public String getLecture_title() {
		return lecture_title;
	}

	public String getLecture_add_time() {
		return lecture_add_time;
	}

	public String getLecture_click_count() {
		return lecture_click_count;
	}

	public String getVideo_local_name() {
		return video_local_name;
	}

	public String getImage_url() {
		return image_url;
	}

	public String getHead_image() {
		return head_image;
	}

	public String getPrice() {
		return price;
	}

	public String getLecture_keyword() {
		return lecture_keyword;
	}

	public String getCategory_id() {
		return category_id;
	}

	public String getChapter_id() {
		return chapter_id;
	}

	public String getLecture_type() {
		return lecture_type;
	}

	public String getLecture_link() {
		return lecture_link;
	}

	public int getLecture_order_id() {
		return lecture_order_id;
	}

	public String getOrder_num() {
		return order_num;
	}

	public int getUser_id() {
		return user_id;
	}

	public String getOrder_state() {
		return order_state;
	}

	public boolean isIs_vip() {
		return is_vip;
	}

	public String getLecture_graphic_content_url() {
		return lecture_graphic_content_url;
	}
}
