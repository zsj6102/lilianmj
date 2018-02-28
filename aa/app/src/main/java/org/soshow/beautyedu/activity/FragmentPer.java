package org.soshow.beautyedu.activity;


import org.soshow.beautyedu.activity.user.PersonInfoActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.RechargesActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.PersonInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.Encryption;
import org.soshow.beautyedu.utils.GsonUtils;

import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 我的
 */
public class FragmentPer extends Fragment implements OnClickListener {
    public static ImageView set_head_icon;
    public static RelativeLayout rl;
    public static TextView tvLogin;
    public static TextView set_name;
    public static boolean isBackToPer;
    private View rootView;
    private SharedPreferences sp;
    private Editor editor;
    private TextView tvRanking;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainTabActivity.title_main.setText("我的");
        sp = getActivity().getSharedPreferences("huizhongjia",
                Context.MODE_PRIVATE);
        editor = sp.edit();

        if (rootView == null) {
//			 rootView = inflater.inflate(R.layout.fragment_per, null);
            rootView = inflater.inflate(R.layout.fragment_person, null);
        }
        if (MyApplication.logined) {
            getInfo();
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个ro已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        LogUtils.e("==========================我的onResume");
        if (RechargesActivity.is_recharge_pay && MyApplication.logined) {
            getInfo();
            tvRanking.setText("等级会员");
            RechargesActivity.is_recharge_pay = false;
        }


        SharedPreferences sp = getActivity().getSharedPreferences(
                "huizhongjia", Context.MODE_PRIVATE);
        set_head_icon = (ImageView) getView().findViewById(R.id.set_head_icon);
        tvLogin = (TextView) getView().findViewById(R.id.person_tv_login);
        set_name = (TextView) getView().findViewById(R.id.set_name);
        tvRanking = (TextView) getView().findViewById(R.id.set_tv_ranking);
        rl = (RelativeLayout) getView().findViewById(R.id.fragment_per_rl);
//		getView().findViewById(R.id.tv_become_year_free).setOnClickListener(
//				this);
        MyApplication.logined = sp.getBoolean("logined", false);
        LogUtils.e("我的     登入标志MyApplication.logined======" + MyApplication.logined);
        if (MyApplication.logined) {
            tvLogin.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);
            set_name.setVisibility(View.VISIBLE);
            if (PersonInfoActivity.isInfoModify) {
                getInfo();
                isBackToPer = true;
                PersonInfoActivity.isInfoModify = false;
            } else {
//				getTokenLocal();
                getInfo();
                String url = (String) SPUtils.get(getActivity(), "headUrl", "");

                String username = (String) SPUtils.get(getActivity(), "nickname", "");
                set_name.setText(username);
                Log.e("", "url=" + url + "");
                UniversalImageLoadTool.disPlayTrue(url, set_head_icon, R.drawable.default_face);
                String user_name = Encryption.desEncryptd(Constant.code_local,
                        Constant.code_local, sp.getString("user_name", null));
                rl.setVisibility(View.VISIBLE);
                set_name.setVisibility(View.VISIBLE);
                tvLogin.setVisibility(View.GONE);
//				String is_vip = (String) SPUtils.get(getActivity(), "is_vip", "");
//				LogUtils.e("我的界面 onStart  is_vip="+is_vip);
//				if(is_vip.equals("0")){
//					tvRanking.setText("普通用户");
//				}else if(is_vip.equals("1")){
//					tvRanking.setText("等级会员");
//				}
            }

        } else {
            set_head_icon.setImageResource(R.drawable.default_face);
            tvLogin.setText(R.string.dlzc);
            tvLogin.setVisibility(View.VISIBLE);
            rl.setVisibility(View.GONE);
            set_name.setVisibility(View.GONE);
        }

        super.onResume();
    }

    // 获取个人资料数据
    private void getInfo() {
        try {
            String url = Constant.PERSON_INFO;
//					+ "&tocken=" + mToken
//					+ "&app_nonce=" + app_nonce;
            Log.e("首页", "个人资料url=" + url);
            NetHelper.get(url, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {
                @Override
                protected void onSuccess(Captcha bean) {
                    if (bean.isSuccess()) {
                        try {
                            PersonInfo personInfo = GsonUtils.parseJSON(bean.info, PersonInfo.class);
                            editor.putString("username", personInfo.getUsername());
//									String is_vip = personInfo.getIs_vip();
//								 	SPUtils.put(getActivity(), "is_vip", is_vip);
//									SPUtils.put(getActivity(), "card_integral", personInfo.getCard_integral());//当前积分
                            SPUtils.put(getActivity(), "headUrl", personInfo.getPhoto_url());
                            SPUtils.put(getActivity(), "username", personInfo.getUsername());
//									SPUtils.put(getActivity(), "signature", personInfo.getSignature());

                            if (MyApplication.logined) {
                                set_name.setText(personInfo.getNickname());
                                String url = (String) SPUtils.get(getActivity(), "headUrl", "");
                                UniversalImageLoadTool.disPlayTrue(url, set_head_icon, R.drawable.default_face);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
//								ToastUtil.getInstance().showToast(getActivity(), bean.message);
                        LogUtils.e("个人资料获取数据异常");
                    }
                }
            });
        } catch (Exception e) {
        }

    }


    @Override
    public void onClick(View v) {
//		Intent intent = new Intent(getActivity(), RechargesActivity.class);
//		startActivity(intent);
//		getActivity().overridePendingTransition(R.anim.anim_slider_right_in,
//                R.anim.anim_slider_left_out);
    }

}