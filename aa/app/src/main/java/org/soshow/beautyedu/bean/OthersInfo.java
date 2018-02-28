package org.soshow.beautyedu.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/6/8.
 */
public class OthersInfo {


    /**
     * username : 静静
     * cell_phone : 15860757523
     * user_id : 907
     * start_time : 0
     * end_time : 0
     * signature : 随心所欲
     * photo : 2016-06/1465371454.png
     * email : 756771956@qq.com
     * is_vip : 0
     * pics : [{"pic_id":"74","dynamic_id":"53","pic_url":"http://192.168.1.112:8088/wap/upload/pics/2016-06/1465365905_6a8.jpg","user_id":"907"},{"pic_id":"73","dynamic_id":"53","pic_url":"http://192.168.1.112:8088/wap/upload/pics/2016-06/1465365905_4kn.jpg","user_id":"907"},{"pic_id":"72","dynamic_id":"53","pic_url":"http://192.168.1.112:8088/wap/upload/pics/2016-06/1465365905.jpg","user_id":"907"},{"pic_id":"71","dynamic_id":"52","pic_url":"http://192.168.1.112:8088/wap/upload/pics/2016-06/1464866726.jpg","user_id":"907"}]
     */

    private String username;
    private String cell_phone;
    private String user_id;
    private String start_time;
    private String end_time;
    private String signature;
    private String photo;
    private String email;
    private String gender;
    private String is_vip;
    /**
     * pic_id : 74
     * dynamic_id : 53
     * pic_url : http://192.168.1.112:8088/wap/upload/pics/2016-06/1465365905_6a8.jpg
     * user_id : 907
     */
    
    

    private List<PicsEntity> pics;

    public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setUsername(String username) {
        this.username = username;
    }

    public void setCell_phone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public void setPics(List<PicsEntity> pics) {
        this.pics = pics;
    }

    public String getUsername() {
        return username;
    }

    public String getCell_phone() {
        return cell_phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getSignature() {
        return signature;
    }

    public String getPhoto() {
        return photo;
    }

    public String getEmail() {
        return email;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public List<PicsEntity> getPics() {
        return pics;
    }

    public static class PicsEntity {
        private String pic_id;
        private String dynamic_id;
        private String pic_url;
        private String user_id;

        public void setPic_id(String pic_id) {
            this.pic_id = pic_id;
        }

        public void setDynamic_id(String dynamic_id) {
            this.dynamic_id = dynamic_id;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPic_id() {
            return pic_id;
        }

        public String getDynamic_id() {
            return dynamic_id;
        }

        public String getPic_url() {
            return pic_url;
        }

        public String getUser_id() {
            return user_id;
        }
    }
}
