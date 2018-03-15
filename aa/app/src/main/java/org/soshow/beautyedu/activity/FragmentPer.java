package org.soshow.beautyedu.activity;


import org.soshow.beautyedu.activity.user.PersonInfoActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.PersonInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
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
    private TextView tvLiNian;
    private View rootView;
    private SharedPreferences sp;
    private Editor editor;
    private TextView set_ranking;
    PersonInfo personInfo;

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
        SharedPreferences sp = getActivity().getSharedPreferences(
                "huizhongjia", Context.MODE_PRIVATE);
        tvLiNian = (TextView) getView().findViewById(R.id.tv_lilian);
        set_head_icon = (ImageView) getView().findViewById(R.id.set_head_icon);
        tvLogin = (TextView) getView().findViewById(R.id.person_tv_login);
        set_name = (TextView) getView().findViewById(R.id.set_name);
        set_ranking = (TextView) getView().findViewById(R.id.set_ranking);
        rl = (RelativeLayout) getView().findViewById(R.id.fragment_per_rl);
        MyApplication.logined = sp.getBoolean("logined", false);
        LogUtils.e("我的     登入标志MyApplication.logined======" + MyApplication.logined);
        if (MyApplication.logined) {
            tvLogin.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);
            set_name.setVisibility(View.VISIBLE);
            getInfo();
            tvLiNian.setVisibility(View.VISIBLE);
        } else {
            set_head_icon.setImageResource(R.drawable.default_face);
            tvLogin.setText(R.string.dlzc);
            tvLogin.setVisibility(View.VISIBLE);
            tvLiNian.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);
            set_name.setVisibility(View.GONE);
        }
        if (PersonInfoActivity.isInfoModify) {
            getInfo();
            isBackToPer = true;
            PersonInfoActivity.isInfoModify = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // 获取个人资料数据
    private void getInfo() {
        try {
            String url = Constant.PERSON_INFO;
            Log.e("首页", "个人资料url=" + url);
            NetHelper.get(url, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {
                @Override
                protected void onSuccess(Captcha bean) {
                    if (bean.isSuccess()) {
                        try {
                            personInfo = GsonUtils.parseJSON(bean.info, PersonInfo.class);
                            if (personInfo.getPhoto_url() != null) {
                                SPUtils.put(getActivity(), "headUrl", personInfo.getPhoto_url());
                            }
                            if (personInfo.getNickname() != null) {
                                SPUtils.put(getActivity(), "username", personInfo.getNickname());
                            }
                            if (personInfo.getSignature() != null) {
                                SPUtils.put(getActivity(), "signature", personInfo.getSignature());
                            }
                            if (MyApplication.logined) {
                                set_name.setText(personInfo.getNickname());
                                set_ranking.setText("当前积分：" + personInfo.getIntegral());
                                tvLiNian.setText("理念号：" + personInfo.getInvitation_code());
                                String url = (String) SPUtils.get(getActivity(), "headUrl", "");
                                Bitmap bm =  drawable2Bitmap(set_head_icon.getDrawable());
                                Resources res = getResources();
                                Bitmap    bmp = BitmapFactory.decodeResource(res, R.drawable.default_face);
                                if (!isEquals(bmp,bm) && !PersonInfoActivity.isChangeOver ) {
                                } else {
                                    UniversalImageLoadTool.disPlayTrue(url, set_head_icon, R.drawable.default_face);
                                    PersonInfoActivity.isChangeOver = false;
                                }
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
    public static Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){//转换成Bitmap
            return ((BitmapDrawable)drawable).getBitmap() ;
        }else if(drawable instanceof NinePatchDrawable){//.9图片转换成Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }else{
            return null ;
        }
    }
    public boolean isEquals(Bitmap b1,Bitmap b2){
        //先判断宽高是否一致，不一致直接返回false
        if(b1.getWidth()==b2.getWidth()
                &&b1.getHeight()==b2.getHeight()){
            int xCount = b1.getWidth();
            int yCount = b1.getHeight();
            for(int x=0; x<xCount; x++){
                for(int y=0; y<yCount; y++){
                    //比较每个像素点颜色
                    if(b1.getPixel(x, y)!=b2.getPixel(x, y)){
                        return false;
                    }
                }
            }
            return true;
        }else{
            return false;
        }
    }
    @Override
    public void onClick(View v) {
    }

}