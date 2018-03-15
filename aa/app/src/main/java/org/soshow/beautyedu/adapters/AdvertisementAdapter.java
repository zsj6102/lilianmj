package org.soshow.beautyedu.adapters;

import java.util.List;

import org.json.JSONArray;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.WebPlayVideoActivity;
import org.soshow.beautyedu.bean.LunBoInfo;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


import com.bumptech.glide.Glide;


/**
 * 广告轮播adapter
 */
public class AdvertisementAdapter extends PagerAdapter {

	private Context context;
	private List<View> views;
	JSONArray advertiseArray;
	private List<LunBoInfo> lunBos;

	public AdvertisementAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AdvertisementAdapter(Context context, List<View> views, JSONArray advertiseArray, List<LunBoInfo> LunBos) {

		this.context = context;
		this.views = views;
		this.advertiseArray = advertiseArray;
		this.lunBos = LunBos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position), 0);
		final int POSITION = position;
		View view = views.get(position);
		ImageView ivAdvertise = (ImageView) view.findViewById(R.id.ivAdvertise);
		try {
			String src = advertiseArray.optJSONObject(position).optString("src");
			if (!src.equals(Integer.toString(position))) {
				//Log.d("123456", "长为" + ivAdvertise.getWidth() + "高为" + ivAdvertise.getHeight());
				// ImageLoaderUtil.getImage(context, ivAdvertise, src,
				// R.drawable.defaultpic, R.drawable.defaultpic, 0, 0);
				//Toast.makeText(context, "Gif测试" + position, Toast.LENGTH_SHORT).show();
				Glide.with(context).load(src).placeholder(R.drawable.defaultpic).into(ivAdvertise);

				//Log.d("1221", "轮播图加载地址 =====" + src);
//				ivAdvertise.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						LogUtil.e("", "轮播地址url1========="+lunBos.get(POSITION).getPhoto_url());
//						if (NetUtil.isConnected(context) && lunBos != null && !lunBos.isEmpty()
//								&& StringUtil.isUrl(lunBos.get(POSITION).getPhoto_url())) {
//							if (StringUtil.isUrl(lunBos.get(POSITION).getPhoto_url())) {
//								Uri content_url;
//								if (lunBos.get(POSITION).getPhoto_url().toLowerCase().startsWith("http")
//										|| lunBos.get(POSITION).getPhoto_url().toLowerCase().startsWith("https")) {
//									content_url = Uri.parse(lunBos.get(POSITION).getPhoto_url());
//								} else {
//									content_url = Uri.parse("http://" + lunBos.get(POSITION).getPhoto_url());
//								}
//
////								Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
////								context.startActivity(intent);
//								LogUtil.e("", "轮播地址url2========="+content_url);
//								Intent intent = new Intent(context,
//										WebPlayVideoActivity.class);
//								intent.putExtra("url", content_url);
//								intent.putExtra("title","lunbo");
//								context.startActivity(intent);
//							} else {
//							}
//
//						} else if (!NetUtil.isConnected(context) && lunBos != null && !lunBos.isEmpty()
//								&& StringUtil.isUrl(lunBos.get(POSITION).getPhoto_url())) {
//							Toast.makeText(context, "请打开您的网络", Toast.LENGTH_SHORT).show();
//						} else {
//							// Toast.makeText(context, "请打开您的网络",
//							// Toast.LENGTH_SHORT).show();
//						}
//					}
//
//				});
			} else {
				switch (position) {
				case 0:
					ivAdvertise.setImageResource(R.drawable.defaultpic);
					// Toast.makeText(context, "Gif测试",
					// Toast.LENGTH_SHORT).show();
					// URI url = new
					// URI("http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif");
					// Glide.with(context).load(
					// "http://img.blog.csdn.net/20150507153918629?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhhbmdwaGls/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center")
					// Glide.with(context).load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
					// .placeholder(R.drawable.defaultpic).into(ivAdvertise);
					// Glide.with(context).load(R.drawable.dongtai).into(ivAdvertise);
					break;

				default:
					break;
				}

			}
		} catch (

		Exception e)

		{
			e.printStackTrace();
		}
		return view;
	}

}
