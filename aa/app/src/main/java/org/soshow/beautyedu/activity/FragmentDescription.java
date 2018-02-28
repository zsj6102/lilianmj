package org.soshow.beautyedu.activity;
/**
 * 视频播放界面简介
 */
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.utils.BCConvert;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentDescription extends BaseFragment {
	private View rootView;
	// private SharedPreferences sp;
	public static FragmentDescription fmt;
	private TextView video_description;
	// private static int count = 0;
	public static boolean mHasLoadedOnce;
	private boolean isPrepared;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fmt = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_description, null);
			video_description = (TextView) rootView.findViewById(R.id.video_description);
			video_description.setVisibility(View.VISIBLE);
			isPrepared = true;
			lazyLoad();

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
		// TODO Auto-generated method stub
		// Log.d("345abc", "title onstart");
		super.onStart();
		// Toast.makeText(getActivity(), "Fragment start" + count,
		// Toast.LENGTH_SHORT).show();
		// count++;
		// Toast.makeText(getActivity(), "onStart", Toast.LENGTH_SHORT).show();
		// video_description = (TextView)
		// getView().findViewById(R.id.video_description);

	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		// if (PlayVideoActivity.list_lecture_play == null ||
		// PlayVideoActivity.list_lecture_play.isEmpty()) {
		video_description.setText(BCConvert
				// .bj2qj(MyApplication.list_lecture.get(PlayVideoActivity.position_video).lecture_content));\
				.bj2qj(PlayVideoActivity.lecture_content));
				// } else {
				// video_description

		// .setText(BCConvert
		// .bj2qj(PlayVideoActivity.list_lecture_play.get(PlayVideoActivity.position_video).lecture_content));
		// }

		mHasLoadedOnce = true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			fmt = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}

}