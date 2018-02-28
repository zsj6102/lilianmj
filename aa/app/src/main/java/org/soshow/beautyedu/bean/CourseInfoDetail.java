package org.soshow.beautyedu.bean;

/**
 * Created by Administrator on 2016/6/8.
 */
public class CourseInfoDetail {


    /**
     * offline_id : 34
     * title : 课程
     * addtime : 1464763193
     * price : 1000
     * content :
     * start_time : 1464710400
     * end_time : 1469980800
     * state : 1
     * img_url : http://192.168.1.112:8088/upload/course_pics/
     * describe : 大师傅
     * publish :
     * max_person : 500
     * school_id : 3
     * category_id : 6
     * vip_price : 800
     * original_price : 1000
     * content_url : http://192.168.1.112:8088/panel/course/offline_course_detail.php?offline_id=19
     * sign_count : 0
     */

    private String offline_id;
    private String title;
    private String addtime;
    private String price;
    private String content;
    private String start_time;
    private String end_time;
    private String state;
    private String img_url;
    private String describe;
    private String publish;
    private String max_person;
    private String school_id;
    private String category_id;
    private String vip_price;
    private String original_price;
    private String content_url;
    private String sign_count;
    private boolean is_vip;
    private String views;//观看人数
    
    
    
    
    public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public boolean isIs_vip() {
		return is_vip;
	}

	public void setIs_vip(boolean is_vip) {
		this.is_vip = is_vip;
	}

	public void setOffline_id(String offline_id) {
        this.offline_id = offline_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public void setMax_person(String max_person) {
        this.max_person = max_person;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setVip_price(String vip_price) {
        this.vip_price = vip_price;
    }

    public void setOriginal_price(String original_price) {
        this.original_price = original_price;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public void setSign_count(String sign_count) {
        this.sign_count = sign_count;
    }

    public String getOffline_id() {
        return offline_id;
    }

    public String getTitle() {
        return title;
    }

    public String getAddtime() {
        return addtime;
    }

    public String getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getState() {
        return state;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getDescribe() {
        return describe;
    }

    public String getPublish() {
        return publish;
    }

    public String getMax_person() {
        return max_person;
    }

    public String getSchool_id() {
        return school_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getVip_price() {
        return vip_price;
    }

    public String getOriginal_price() {
        return original_price;
    }

    public String getContent_url() {
        return content_url;
    }

    public String getSign_count() {
        return sign_count;
    }
}
