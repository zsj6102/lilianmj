package org.soshow.beautyedu.activity;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.FileSizeUtil;
import org.soshow.beautyedu.utils.MyAlertDialog;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 设置
 */
public class FragmentSet extends Fragment {
	private View rootView;
	private RelativeLayout logout_set;
	// 设置网络复选框
	public static CheckBox allow_net;
	private SharedPreferences sp;
	private Editor editor;
	private int allow_net_local;
	public static TextView cache_size;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_set, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		logout_set = (RelativeLayout) getView().findViewById(R.id.logout_set);
		allow_net = (CheckBox) getView().findViewById(R.id.allow_net);
		cache_size = (TextView) getView().findViewById(R.id.cache_size);
		cache_size.setText(FileSizeUtil.getFileOrFilesSize(getActivity().getCacheDir().toString(), 3) + "M");
		sp = getActivity().getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		allow_net_local = sp.getInt("allow_net_local", 0);
		if (allow_net_local == 1) {
			allow_net.setChecked(true);
		}
		MyApplication.logined = sp.getBoolean("logined", false);
		if (MyApplication.logined) {
			logout_set.setVisibility(View.VISIBLE);
		} else {
			logout_set.setVisibility(View.GONE);
		}

		allow_net.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					if (!Constant.allow_net_other) {
						Builder bd = new MyAlertDialog.Builder(getActivity());
						bd.setTitle("网络提示").setMessage(R.string.video_net_warn)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.dismiss();
								allow_net.setChecked(true);

								editor.putInt("allow_net_local", 1);
								editor.commit();
								// getTokenLocal();
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								try {
									dialog.dismiss();
								} catch (Exception e) {
									// TODO: handle exception
								}
								allow_net.setChecked(false);
								editor.putInt("allow_net_local", 0);
								editor.commit();
							}
						}).show();
					} else {
						Constant.allow_net_other = false;
					}
				} else {
					allow_net.setChecked(false);
					editor.putInt("allow_net_local", 0);
					editor.commit();
				}

			}

		});
	}
}