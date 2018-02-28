package org.soshow.beautyedu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/4/29.
 */
public class VideoCourseInfo implements Serializable{


    /**
     * lecture_id : 188
     * lecture_title : 名姿
     * lecture_content :
     * lecture_click_count : 169
     * lecture_add_time : 2016-07-06 17:20:50
     * video_local_name :
     * play_urls : [{"url":"http://200024434.vod.myqcloud.com/200024434_3b832abe6a6011e6ad65bdedea22a965.f0.mp4","definition":0,"vbitrate":0,"vheight":0,"vwidth":0},{"url":"http://200024434.vod.myqcloud.com/200024434_3b832abe6a6011e6ad65bdedea22a965.f20.mp4","definition":20,"vbitrate":48383,"vheight":360,"vwidth":640}]
     * image_url : 768a7920160706.jpg
     * head_image : http://myjy.xianshan.cn/upload/video_pic/s_768a7920160706.jpg
     * price : 78.00
     * lecture_keyword : 美甲
     * category_id : 3
     * chapter_id : 0
     * lecture_type : 1
     * lecture_link :
     * lecture_order_id : 0
     * order_num : 0
     * user_id : 0
     * order_state : 0
     * is_vip : false
     * lecture_content_url : http://myjy.xianshan.cn/wap/panel/other/lecture_detail.php?type=lecture_content&lecture_id=188
     * lecture_graphic_content_url :
     */

    private String lecture_id;
    private String lecture_title;
    private String lecture_content;
    private String lecture_click_count;
    private String lecture_add_time;
    private String video_local_name;
    private String image_url;
    private String head_image;
    private String price;
    private String lecture_keyword;
    private String category_id;
    private String chapter_id;
    private String lecture_type;
    private String lecture_link;
    private String lecture_order_id;
    private String order_num;
    private String user_id;
    private String order_state;
    private boolean is_vip;
    private String lecture_content_url;
    private String lecture_graphic_content_url;
    private String lecture_integral;
    private String teacher_id;
    private String teacher_name;
    /**
     * url : http://200024434.vod.myqcloud.com/200024434_3b832abe6a6011e6ad65bdedea22a965.f0.mp4
     * definition : 0
     * vbitrate : 0
     * vheight : 0
     * vwidth : 0
     */
    
    

    private List<PlayUrlsEntity> play_urls;

    
    
    public String getTeacher_id() {
		return teacher_id;
	}

	public void setTeacher_id(String teacher_id) {
		this.teacher_id = teacher_id;
	}

	public String getTeacher_name() {
		return teacher_name;
	}

	public void setTeacher_name(String teacher_name) {
		this.teacher_name = teacher_name;
	}

	public String getLecture_integral() {
		return lecture_integral;
	}

	public void setLecture_integral(String lecture_integral) {
		this.lecture_integral = lecture_integral;
	}

	public void setLecture_id(String lecture_id) {
        this.lecture_id = lecture_id;
    }

    public void setLecture_title(String lecture_title) {
        this.lecture_title = lecture_title;
    }

    public void setLecture_content(String lecture_content) {
        this.lecture_content = lecture_content;
    }

    public void setLecture_click_count(String lecture_click_count) {
        this.lecture_click_count = lecture_click_count;
    }

    public void setLecture_add_time(String lecture_add_time) {
        this.lecture_add_time = lecture_add_time;
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

    public void setLecture_order_id(String lecture_order_id) {
        this.lecture_order_id = lecture_order_id;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setOrder_state(String order_state) {
        this.order_state = order_state;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public void setLecture_content_url(String lecture_content_url) {
        this.lecture_content_url = lecture_content_url;
    }

    public void setLecture_graphic_content_url(String lecture_graphic_content_url) {
        this.lecture_graphic_content_url = lecture_graphic_content_url;
    }

    public void setPlay_urls(List<PlayUrlsEntity> play_urls) {
        this.play_urls = play_urls;
    }

    public String getLecture_id() {
        return lecture_id;
    }

    public String getLecture_title() {
        return lecture_title;
    }

    public String getLecture_content() {
        return lecture_content;
    }

    public String getLecture_click_count() {
        return lecture_click_count;
    }

    public String getLecture_add_time() {
        return lecture_add_time;
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

    public String getLecture_order_id() {
        return lecture_order_id;
    }

    public String getOrder_num() {
        return order_num;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getOrder_state() {
        return order_state;
    }

    public boolean isIs_vip() {
        return is_vip;
    }

    public String getLecture_content_url() {
        return lecture_content_url;
    }

    public String getLecture_graphic_content_url() {
        return lecture_graphic_content_url;
    }

    public List<PlayUrlsEntity> getPlay_urls() {
        return play_urls;
    }

    public static class PlayUrlsEntity implements Serializable{
        private String url;
        private int definition;
        private int vbitrate;
        private int vheight;
        private int vwidth;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setDefinition(int definition) {
            this.definition = definition;
        }

        public void setVbitrate(int vbitrate) {
            this.vbitrate = vbitrate;
        }

        public void setVheight(int vheight) {
            this.vheight = vheight;
        }

        public void setVwidth(int vwidth) {
            this.vwidth = vwidth;
        }

        public String getUrl() {
            return url;
        }

        public int getDefinition() {
            return definition;
        }

        public int getVbitrate() {
            return vbitrate;
        }

        public int getVheight() {
            return vheight;
        }

        public int getVwidth() {
            return vwidth;
        }
    }
}
