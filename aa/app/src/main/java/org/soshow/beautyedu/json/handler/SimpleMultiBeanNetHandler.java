package org.soshow.beautyedu.json.handler;

import org.soshow.beautyedu.json.bean.NetBaseBean;
import org.soshow.beautyedu.json.utils.NetToastUtil;

import android.content.Context;

/**
 * 
 * 简单的返回是多个的（List）网络请求Handler，子类只要实现onSuccess方法
 * 
 * @author wuxiaohong
 * @date 2015-7-15 上午11:22:12
 * 
 */
public abstract class SimpleMultiBeanNetHandler<T extends NetBaseBean> extends MultiBeanNetHandler<T> {

	private Context mContext;

	public SimpleMultiBeanNetHandler(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	protected void onError(int errorCode, String errorMsg) {
		NetToastUtil.errorToast(mContext, errorCode, errorMsg);
	}

}
