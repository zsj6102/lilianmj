package org.soshow.beautyedu.bean;

import java.io.Serializable;

/**
 * video_carid : "39", //积分卡id card_integrals : "200", //积分卡赠送积分（这个积分可以为0）
 * card_integral : "200", //积分卡积分(卡的总积分由（积分卡积分分和积分卡赠送积分相加）) card_imgurl :
 * "http://myjy.xianshan.cn/upload/video_card/d0ee0920161012.jpg",(积分卡图片地址)
 * card_title : "dd",（积分卡的描述） card_name : "dd"(积分卡的名称)
 * 
 * @author chenjiaming
 *
 */
public class IntegerInfo implements Serializable{
	private String video_carid;
	private String card_title;
	private String price;
	private String cost_price;
	private String status;//1：上架    0：下架
	private String pay_num;
	private String card_imgurl;
	private String card_integral;
	private String card_name;
	private String info_url;
	
	


	public String getInfo_url() {
		return info_url;
	}



	public void setInfo_url(String info_url) {
		this.info_url = info_url;
	}



	public String getPrice() {
		return price;
	}



	public void setPrice(String price) {
		this.price = price;
	}



	public String getCost_price() {
		return cost_price;
	}



	public void setCost_price(String cost_price) {
		this.cost_price = cost_price;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getPay_num() {
		return pay_num;
	}

	public void setPay_num(String pay_num) {
		this.pay_num = pay_num;
	}

	public String getVideo_carid() {
		return video_carid;
	}

	public void setVideo_carid(String video_carid) {
		this.video_carid = video_carid;
	}


	public String getCard_integral() {
		return card_integral;
	}

	public void setCard_integral(String card_integral) {
		this.card_integral = card_integral;
	}

	public String getCard_imgurl() {
		return card_imgurl;
	}

	public void setCard_imgurl(String card_imgurl) {
		this.card_imgurl = card_imgurl;
	}

	public String getCard_title() {
		return card_title;
	}

	public void setCard_title(String card_title) {
		this.card_title = card_title;
	}

	public String getCard_name() {
		return card_name;
	}

	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

}
