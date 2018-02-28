package org.soshow.beautyedu.activity;

import java.util.ArrayList;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.widget.FragmentAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * ViewPager + Fragment + FragmentPagerAdapter
 * 我的收藏页面
 * @since 2015��1��5��
 * 
 * 
 */
public class CollectActivity extends FragmentActivity implements OnClickListener, OnPageChangeListener {

	// ����textview
	private TextView tab1Tv, tab2Tv;
	// ָʾ��
	private ImageView cursorImg;
	// viewpager
	private ViewPager viewPager;
	// fragment���󼯺�
	private ArrayList<Fragment> fragmentsList;
	// ��¼��ǰѡ�е�tab��index
	//private int currentIndex = 0;
	// ָʾ����ƫ����
	private int offset = 0;
	// ��margin
	//private int leftMargin = 0;
	// ��Ļ���
	private int screenWidth = 0;
	// ��Ļ��ȵ�����֮һ
	private int screen1_3;
	//
	private LinearLayout.LayoutParams lp;
	//public static Dialog dialog;
	private TextView title_name;
	// public static Boolean refresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_collect);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("我的收藏");
		//dialog = ProgressDialogUtil.createLoadingDialog(CollectActivity.this, null, true, false);
		// refresh = true;
		init();
	}

	/**
	 * ��ʼ������
	 */
	private void init() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screen1_3 = screenWidth / 2;

		cursorImg = (ImageView) findViewById(R.id.cursor);
		lp = (LayoutParams) cursorImg.getLayoutParams();
		//leftMargin = lp.leftMargin;

		tab1Tv = (TextView) findViewById(R.id.tab1_tv);
		tab2Tv = (TextView) findViewById(R.id.tab2_tv);
		tab1Tv.setOnClickListener(this);
		tab2Tv.setOnClickListener(this);
		//

		initViewPager();
	}

	/**
	 * ��ʼ��viewpager
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.third_vp);
		fragmentsList = new ArrayList<Fragment>();
		Fragment fragment = new FragmentMyVideo();
		fragmentsList.add(fragment);
		fragment = new FragmentMyTitle();
		fragmentsList.add(fragment);

		viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentsList));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab1_tv:
			// Toast.makeText(CollectActivity.this, "tab1_tv点击",
			// Toast.LENGTH_SHORT).show();
			tab1Tv.setTextColor(getResources().getColor(R.color.blue));
			tab2Tv.setTextColor(getResources().getColor(R.color.word_grey));
			viewPager.setCurrentItem(0);
			break;
		case R.id.tab2_tv:
			// Toast.makeText(CollectActivity.this, "tab1_tv点击",
			// Toast.LENGTH_SHORT).show();
			tab2Tv.setTextColor(getResources().getColor(R.color.blue));
			tab1Tv.setTextColor(getResources().getColor(R.color.word_grey));
			viewPager.setCurrentItem(1);
			break;
		case R.id.back_search_logininput:
			
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int position) {
		//Log.d("123456", "onPageScrollStateChanged" + position);

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		offset = (screen1_3 - cursorImg.getLayoutParams().width) / 2;
		//Log.d("111", position + "--" + positionOffset + "--" + positionOffsetPixels);
		//final float scale = getResources().getDisplayMetrics().density;
		// Log.d("123456", "position="+position+"
		// positionOffsetPixels"+positionOffsetPixels+"
		// positionOffset"+positionOffset);
		if (position == 0) {// 0<->1
			lp.leftMargin = (int) (positionOffsetPixels / 2) + offset;

		} else if (position == 1) {// 1<->2

			lp.leftMargin = (int) (positionOffsetPixels / 2) + screen1_3 + offset;
		}
		cursorImg.setLayoutParams(lp);
		//currentIndex = position;
	}

	@Override
	public void onPageSelected(int position) {
		//Log.d("123456", "onPageSelected" + position);
		if (position == 0) {
			tab1Tv.setTextColor(getResources().getColor(R.color.blue));
			tab2Tv.setTextColor(getResources().getColor(R.color.word_grey));
		} else if (position == 1) {
			tab2Tv.setTextColor(getResources().getColor(R.color.blue));
			tab1Tv.setTextColor(getResources().getColor(R.color.word_grey));
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		// refresh = false;

	//	try {
	//		if (dialog != null) {
	//			dialog.dismiss();
	//		}
	//	} catch (Exception e) {
			// TODO: handle exception
	//	}
		FragmentMyVideo.vca=null;
		FragmentMyVideo.listItems=null;
		FragmentMyTitle.sa=null;
		FragmentMyTitle.listItems=null;
		super.finish();
	}

}
