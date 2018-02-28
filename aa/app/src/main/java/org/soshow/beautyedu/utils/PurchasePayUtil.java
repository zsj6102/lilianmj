     package org.soshow.beautyedu.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.Xml;

/**
 * @Package: com.soshow.hiyoga.utils
 * @Author: chenjiaming
 * @Time: 2015年10月15日 上午9:33:17
 * @File: PurchasePayUtil.java
 * @Description: 支付工具类
 */
public class PurchasePayUtil {

    public static final String PAY_WEIXINPAY = "weixinPay";
    public static final String WINXIN_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    public static final String WINXIN_PAY = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    private static PurchasePayUtil purchasePayUtil;
    private Context context;

    private PurchasePayUtil(Context context) {
        this.context = context;
    }

    public static PurchasePayUtil getInstance(Context context) {
        if (purchasePayUtil == null) {
            synchronized (PurchasePayUtil.class) {
                if (purchasePayUtil == null) {
                    purchasePayUtil = new PurchasePayUtil(context);
                }
            }

        }
        return purchasePayUtil;
    }

    /**
     * 
     * @Author: chenjiaming
     * @Time: 2015年10月15日 上午9:40:07
     * @return
     * @ReturnType: String
     * @Description: 获取ip地址
     */
    public String gainIpAddress() {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    /**
     * 生成签名
     */
    public String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constant.API_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
                .toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    // public String genAppSign(List<NameValuePair> params) {
    // StringBuilder sb = new StringBuilder();
    //
    // for (int i = 0; i < params.size(); i++) {
    // sb.append(params.get(i).getName());
    // sb.append('=');
    // sb.append(params.get(i).getValue());
    // sb.append('&');
    // }
    // sb.append("key=");
    // sb.append(Constants.API_KEY);
    // sb.append("sign str\n"+sb.toString()+"\n\n");
    // String appSign =
    // MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
    // Log.e("orion",appSign);
    // return appSign;
    // }
    public String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                case XmlPullParser.START_DOCUMENT:

                    break;
                case XmlPullParser.START_TAG:

                    if ("xml".equals(nodeName) == false) {
                        // 实例化student对象
                        xml.put(nodeName, parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;
    }

    public static String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
                .getBytes());
    }

    public static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constant.API_KEY);
        String appSign = MD5.getMessageDigest(sb.toString().getBytes())
                .toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

}
