package org.soshow.beautyedu.bean;

/**
 * Created by Administrator on 2016/6/6.
 */
public class SchoolList {

    /**
     * school_id : 3
     * name : 厦门育才小学
     * address : 海沧区
     * addtime : 1464681620
     * state : 1
     * phone : 0592-3265986
     * introduction :
     * faculty :
     * pic_url : http://192.168.1.112:8088/upload/course_pics/
     * introduction_url : http://192.168.1.112:8088/panel/course/offline_course_detail.php?type=introduction&school_id=3
     * faculty_url : http://192.168.1.112:8088/panel/course/offline_course_detail.php?type=faculty&school_id=3
     */

    private String school_id;
    private String name;
    private String address;
    private String addtime;
    private String state;
    private String phone;
    private String introduction;
    private String faculty;
    private String pic_url;
    private String introduction_url;
    private String faculty_url;

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public void setIntroduction_url(String introduction_url) {
        this.introduction_url = introduction_url;
    }

    public void setFaculty_url(String faculty_url) {
        this.faculty_url = faculty_url;
    }

    public String getSchool_id() {
        return school_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAddtime() {
        return addtime;
    }

    public String getState() {
        return state;
    }

    public String getPhone() {
        return phone;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getIntroduction_url() {
        return introduction_url;
    }

    public String getFaculty_url() {
        return faculty_url;
    }
}
