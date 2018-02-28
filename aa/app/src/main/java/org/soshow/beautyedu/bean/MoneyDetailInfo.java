package org.soshow.beautyedu.bean;

public class MoneyDetailInfo {
	
	
	private String order_id;
	private String user_id;
	private String goods_name;
	private String cash;
	private String describe;
	private String addtime;
	private String type;// 0-收入，1-支出
	private String bank_card_id;
	private String is_deal;
	private String deal_time;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBank_card_id() {
		return bank_card_id;
	}

	public void setBank_card_id(String bank_card_id) {
		this.bank_card_id = bank_card_id;
	}

	public String getIs_deal() {
		return is_deal;
	}

	public void setIs_deal(String is_deal) {
		this.is_deal = is_deal;
	}

	public String getDeal_time() {
		return deal_time;
	}

	public void setDeal_time(String deal_time) {
		this.deal_time = deal_time;
	}

}
