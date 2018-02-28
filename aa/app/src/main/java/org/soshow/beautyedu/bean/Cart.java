package org.soshow.beautyedu.bean;

import java.io.Serializable;

/**
 * @Package: org.soshow.mingzi.bean
 * @Author: caixiaozhen
 * @Time: 2015年10月9日 下午6:13:13
 * @File: CardListInfo.java
 * @Description: 购物车列表
 */
public class Cart implements Serializable {

	/**
	 * id : "15", goods_id : "36", //物品id号 goods_num : "6", //物品数量 addtime :
	 * "1476774384",//添加购物车时间 imgurl :
	 * "http://myjy.xianshan.cn/upload/video_card/d0ee0920161012.jpg",//物品图片地址
	 * price : 120, //物品总价 one_price : "20.00", //单个物品价格 card_name : "dd",//物品名称
	 * card_title : "dd", //物品描述 integral : 2000 //物品价值多少积分
	 * 
	 * @Description: None
	 */
	private static final long serialVersionUID = -8484545760824882087L;
	private String id;
	private String goods_id;
	private int goods_num;
	private String addtime;
	private String imgurl;
	private double price;// 价格
	private String one_price;
	private String card_name;
	private String card_title;
	private String integral;
	private String status;

	private boolean isSelect;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public int getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(int goods_num) {
		this.goods_num = goods_num;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getOne_price() {
		return one_price;
	}

	public void setOne_price(String one_price) {
		this.one_price = one_price;
	}

	public String getCard_name() {
		return card_name;
	}

	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

	public String getCard_title() {
		return card_title;
	}

	public void setCard_title(String card_title) {
		this.card_title = card_title;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
