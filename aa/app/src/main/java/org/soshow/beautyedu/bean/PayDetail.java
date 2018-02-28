package org.soshow.beautyedu.bean;

/**
 * 支付详情
 * 
 * @author chenjiaming card_integral : 3033, card_name : "3000积分卡",
 *         card_imageurl :
 *         "http://myjy.xianshan.cn/upload/video_card/f4298a20161012.jpg",
 *         goods_id : "37", price : "20.00", card_title : "价值3000积分", status :
 *         "1", goods_num : 3， shopcart_id：18
 */
public class PayDetail {
	private String card_integral;
	private String card_name;
	private String card_imageurl;
	private String goods_id;
	private String price;
	private String card_title;
	private String status;
	private String goods_num;
	private String shopcart_id;
	private String order_sn;
	
	

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getCard_integral() {
		return card_integral;
	}

	public void setCard_integral(String card_integral) {
		this.card_integral = card_integral;
	}

	public String getCard_name() {
		return card_name;
	}

	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

	public String getCard_imageurl() {
		return card_imageurl;
	}

	public void setCard_imageurl(String card_imageurl) {
		this.card_imageurl = card_imageurl;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCard_title() {
		return card_title;
	}

	public void setCard_title(String card_title) {
		this.card_title = card_title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(String goods_num) {
		this.goods_num = goods_num;
	}

	public String getShopcart_id() {
		return shopcart_id;
	}

	public void setShopcart_id(String shopcart_id) {
		this.shopcart_id = shopcart_id;
	}

}
