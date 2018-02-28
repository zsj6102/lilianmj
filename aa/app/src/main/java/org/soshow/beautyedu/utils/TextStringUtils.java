package org.soshow.beautyedu.utils;

import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 作�?�：LigthWang
 * @version 1.0
 * @date 创建时间�?2015�?8�?29�? 下午4:57:12
 * @parameter 内容描述：判断文本的类型
 * @return
 */
public class TextStringUtils {

    public static boolean isEditText(EditText editText) {

        return isEmpty(editText.getText().toString().trim());
    }

    public static boolean firstStr(String str) {
        Pattern pattern = Pattern.compile("[A-Za-z]");
        Matcher mc = pattern.matcher(str);
        return mc.matches();
    }

    /**
     * 判断字符串是否为�?
     */
    public static boolean isEmpty(String str) {

        return str == null || "".equals(str) || "null".equals(str);
    }

    /**
     * 判断字符串是否为邮箱
     */
    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher mc = pattern.matcher(str);
        return mc.matches();
    }

    // 验证电话号码
    public static boolean isMobileNO(String phoneNumber) {
        if (TextStringUtils.isEmpty(phoneNumber) == true) {
            return false;
        }
        String expression = "^1[0-9]{10}$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断用户输入的内容是否是汉字
     */
    public static boolean isHanzi(String text) {
        int len = 0;
        char[] charText = text.toCharArray();
        for (int i = 0; i < charText.length; i++) {
            if (isChinese(charText[i])) {
                len += 2;
            } else {

                return false;
            }
        }
        return true;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 验证身份证号是否符合规则
     *
     * @param text 身份证号
     * @return
     */
    public static boolean ispersonIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx) || text.matches(reg1) || text.matches(regex);
    }

    /**
     * 判断List是否为空
     */
    public static boolean listIsNullOrEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 判断map是否为空
     */
    public static boolean mapIsNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty() || map.size() == 0;
    }

    /**
     * 判断object是否为空
     */
    public static boolean objectIsNull(Object object) {
        return object == null;
    }

    /**
     * 将字符串的编码转换成utf�?8
     *
     * @param str
     * @return
     */
    public static String encodeString(String str) {
        if (str == null) {
            str = "";
        } else if (str.equals("") || str.length() == 0) {
            str = "";
        } else {
            try {
                str = URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String decodeUtf8(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    //小写判断
    public static boolean isLower(int ch) {
        return ((ch - 'a') | ('z' - ch)) >= 0;
    }

    //大写判断
    public static boolean isUpper(int ch) {
        return ((ch - 'A') | ('Z' - ch)) >= 0;
    }

    //判断密码是否由数字或者字母组成
    public static boolean IsPassword(String password) {
        boolean isRight = false;
        for (int i = 0; i < password.length(); i++) {
            String str = password.substring(i, i + 1);
            String reg1 = "^[A-Za-z]+$";
            String reg2 = "^[0-9]*$";
            isRight = str.matches(reg1) || str.matches(reg2);
            if (isRight == false) {
                break;
            }
        }
        return isRight;
    }

    public static String ReturnUrl(String host, HashMap<String, String> params) {
        String st = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            st += k + "=" + v + "&";
        }
        if (st.length() > 0) {
            st = st.substring(0, st.length() - 1);
        }
        return host + "?" + st;

    }

    /**
     * 正则表达式：判断是否数字	 * @param str	 * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
