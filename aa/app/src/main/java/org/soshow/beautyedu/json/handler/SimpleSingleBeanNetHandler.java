package org.soshow.beautyedu.json.handler;

import org.soshow.beautyedu.json.bean.NetBaseBean;
import org.soshow.beautyedu.json.utils.NetToastUtil;

import android.content.Context;

/**
 * 
 * 简单的返回是单个的（单个Bean）网络请求Handler， 子类只要实现onSuccess方法
 * 
 * @author wuxiaohong
 * @date 2015-7-15 上午11:22:12
 * 
 */
public abstract class SimpleSingleBeanNetHandler<T extends NetBaseBean> extends SingleBeanNetHandler<T> {

	private Context mContext;

	public SimpleSingleBeanNetHandler(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onError(int errorCode, String errorMsg) {
		NetToastUtil.errorToast(mContext, errorCode, errorMsg);
	}

}
