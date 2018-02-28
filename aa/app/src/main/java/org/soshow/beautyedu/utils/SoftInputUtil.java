package org.soshow.beautyedu.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftInputUtil {

	/**
	 * 隐藏显示的软键盘
	 */
//	public static void hideSoftInput(Context context){
//		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//		boolean isOpen = imm.isActive();
//		if(isOpen){
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//		}
//	}
	
	/**
	 * 强制隐藏软键盘
	 */
	public static void hideSoftInput(Context context){
		//1.得到InputMethodManager对象
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				//2.调用showSoftInput方法显示软键盘，其中view为聚焦的view组件
				imm.toggleSoftInput(0, imm.RESULT_HIDDEN); 
		
	}
	
	/**
	 * 显示软键盘
	 */
	public static void showSoftInput(Context context){
		//1.得到InputMethodManager对象
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		//2.调用showSoftInput方法显示软键盘，其中view为聚焦的view组件
		imm.toggleSoftInput(0, imm.HIDE_NOT_ALWAYS); 
	}
	
	
	
	
}
