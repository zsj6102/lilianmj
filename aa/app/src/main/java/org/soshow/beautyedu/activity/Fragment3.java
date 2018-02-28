package org.soshow.beautyedu.activity;
/**
 * 引导页3
 */
import org.soshow.beautyedu.R;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment3 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_3, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		TextView textView = (TextView) getView().findViewById(R.id.tvInNew);

		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 保存数据到SharePreference中以此判断是不是第一次登陆
				// getActivity().finish();
				if(LauncherActivity.la_ins!=null){
					Message message=LauncherActivity.la_ins.getHandler().obtainMessage();
					message.what=8;
					message.sendToTarget();
				}
			}
		});
	}
}
