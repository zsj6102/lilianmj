package org.soshow.beautyedu.bean;


/**
 * Created by Administrator on 2018/2/8 0008.
 */

public class WxCallbackInfo {
    private String  isFirstLogin;
    private String openId;
    private PersonInfo userInfo;

    public String getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(String isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public PersonInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(PersonInfo userInfo) {
        this.userInfo = userInfo;
    }
}
