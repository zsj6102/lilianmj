package org.soshow.beautyedu.bean;

import java.io.Serializable;

public class BankInfo implements Serializable{
	private String bank_card_id;
	private String user_id;
	private String bank_number;
	private String bank_name;
	private String realname;
	private String addtime;
	private String mobile;

	public String getBank_card_id() {
		return bank_card_id;
	}

	public void setBank_card_id(String bank_card_id) {
		this.bank_card_id = bank_card_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getBank_number() {
		return bank_number;
	}

	public void setBank_number(String bank_number) {
		this.bank_number = bank_number;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
