package org.soshow.beautyedu.json.handler;

import java.lang.reflect.ParameterizedType;



import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.json.bean.NetBaseBean;

/**
 * 
 * 返回带Bean的网络请求Handler，这个类实际中不会去用它，他的子类才有用
 * 
 * @author wuxioahong
 * @date 2015-7-17 下午4:42:39
 * 
 */
abstract public class BaseBeanNetHandler<T extends NetBaseBean> extends BaseNetHandler {

	protected T parseItem(JSONObject jsonObject) throws JSONException {
		T t = getBean();
		t.initByJson(jsonObject);
		return t;
	}

	@SuppressWarnings("unchecked")
	protected T getBean() {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		T entity = null;
		try {
			entity = (T) entityClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entity;
	}

}
