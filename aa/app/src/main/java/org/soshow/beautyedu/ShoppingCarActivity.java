package org.soshow.beautyedu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.bean.Cart;
import org.soshow.beautyedu.bean.IntegerInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.LoadingDialogShow;
import org.soshow.beautyedu.widget.MyDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Package: org.soshow.mingzi.personalcenter
 * @Author: huangenqiang
 * @Time: 2015年8月28日 下午5:31:53
 * @Description: 购物车页面
 */
public class ShoppingCarActivity extends Activity implements OnClickListener {
	public static final int CLICK_CHECKBOX = 100;
    private ImageView ivBack;
    private TextView tvTitle;
    private ListView lvContent;
    private ArrayList<Cart> carts;
    private static CheckBox checkAll;
    private Context context;
    private TextView tvSum;
    private TextView tvNoData;
    private String goods_id;//商品id
	private String app_nonce;
	private String mToken;
	private LinearLayout loading;
	private CommonAdapter<Cart> adapter;
	private SharedPreferences sp;
	private Editor editor;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						// photoInfos.clear();
						iniData();
					} else {
						Toast.makeText(ShoppingCarActivity.this,
								"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						if (ShoppingCarActivity.this != null) {
							Toast.makeText(ShoppingCarActivity.this,
									"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
            case CLICK_CHECKBOX:
                judgeCheckAll();
                break;
            default:
                break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_shoppingcar);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = ShoppingCarActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
        initView();
        getTokenLocal();

    }

    private void initView() {
        carts = new ArrayList<Cart>();
        TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(R.string.my_shopping_car);
		loading = (LinearLayout) findViewById(R.id.linear_load);
        tvNoData = (TextView) findViewById(R.id.shoppingCard_tv_noData);
        loading.setVisibility(View.VISIBLE);

        checkAll = (CheckBox) findViewById(R.id.shoppingCar_chkb_all);
        checkAll.setOnClickListener(this);
        tvSum = (TextView) findViewById(R.id.shoppingCar_tv_sum_money);
        lvContent = (ListView) findViewById(R.id.shoppingCar_lv_content);
        findViewById(R.id.shoppingCar_btn_balance).setOnClickListener(this);
//        tvSum.setText(String.format(
//                this.getResources().getString(R.string.money_describe), "0.00"));
        adapter = new CommonAdapter<Cart>(context, carts,
				R.layout.item_shoppingcar_list) {

			@Override
			public void convert(ViewHolder viewHolder, Cart cart) {
		        CheckBox ckIsSelect = viewHolder
		                .getView(R.id.shoppingCarItem_radioBtn_select);
		        ImageView ivPtoto = viewHolder.getView(R.id.shoppingCarItem_iv_photo);
		        TextView tvName = viewHolder.getView(R.id.shoppingCarItem_tv_name);
		        LinearLayout llDown = viewHolder.getView(R.id.shoppingCard_ll_down);
		        final EditText editNum = viewHolder.getView(R.id.shoppingCard_edi_num);
		        LinearLayout llUp = viewHolder.getView(R.id.shoppingCard_ll_up);
		        TextView tvNewPrice = viewHolder.getView(R.id.shoppingCarItem_tv_new_money);
		        ImageView tvDel = viewHolder.getView(R.id.shoppingCarItem_iv_del);
		        
		        TextView tvChooseNum = viewHolder.getView(R.id.tv_item_shoping_card_num);
		        TextView tvPerMoney = viewHolder.getView(R.id.tv_item_shoping_card_money);
		        
		        
		        editNum.setText(String.valueOf(cart.getGoods_num()));
		        llDown.setOnClickListener(new CartClickListener(carts, viewHolder.getPosition()));
		        llUp.setOnClickListener(new CartClickListener(carts, viewHolder.getPosition()));
		        ckIsSelect.setOnClickListener(new CartClickListener(carts, viewHolder.getPosition()));
		        tvDel.setOnClickListener(new CartClickListener(carts, viewHolder.getPosition()));
		        UniversalImageLoadTool.disPlayTrue(cart.getImgurl(),
		                ivPtoto, R.drawable.default_pic);
		        tvName.setText(cart.getCard_title());
		        editNum.setText(String.valueOf(cart.getGoods_num()));
		        tvChooseNum.setText("选择"+cart.getGoods_num()+"件商品");
		        ckIsSelect.setChecked(cart.isSelect());
		        DecimalFormat decimalFormat = new DecimalFormat("0.00");
		        if(!TextUtils.isEmpty(cart.getOne_price())){
		        	tvNewPrice.setText(String.format(
		        			context.getResources().getString(R.string.money_describe),
		        			String.valueOf(decimalFormat.format(Double.valueOf(cart.getOne_price())))));
		        	double perMoney = cart.getGoods_num()*Double.valueOf(cart.getOne_price());
		        	tvPerMoney.setText(String.format(context.getResources().getString(R.string.money_describe),
		        			String.valueOf(decimalFormat.format(perMoney))));
		        }else{
		        	tvNewPrice.setText("￥0.00");
		        	tvPerMoney.setText("￥0.00");
		        }
		    }
		};
		lvContent.setAdapter(adapter);
    }

    // 判断是否全部选中
    private void judgeCheckAll() {
        DecimalFormat df = new DecimalFormat("0.00");
        double total = 0.0;
        int checkNumber = 0;
        for (int i = 0; i < carts.size(); i++) {
            if (carts.get(i).isSelect()) {
                checkNumber++;
                total = carts.get(i).getPrice() * carts.get(i).getGoods_num() + total;
            }
        }
        tvSum.setText(String.format(
                this.getResources().getString(R.string.money_describe),
                String.valueOf(df.format(total))));
        if (checkNumber == carts.size()) {
            checkAll.setChecked(true);
        } else {
            checkAll.setChecked(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
        case R.id.shoppingCar_chkb_all:
            double total = 0.0;
            boolean isCheckAll = checkAll.isChecked();
            for (int i = 0; i < carts.size(); i++) {
                carts.get(i).setSelect(isCheckAll);
                if (carts.get(i).isSelect()) {
                    total = carts.get(i).getPrice() * carts.get(i).getGoods_num()
                            + total;
                }
            }
            if (total == 0.0) {
                tvSum.setText(String.format(
                        this.getResources().getString(R.string.money_describe),
                        "0.00"));
            } else {
                tvSum.setText(String
                        .format(this.getResources().getString(
                                R.string.money_describe), String
                                .valueOf(new DecimalFormat("0.00")
                                        .format(total))));
            }
            adapter.notifyDataSetChanged();
            break;
        case R.id.shoppingCar_btn_balance:
            settlement();
            break;
        default:
            break;
        }

    }

    // 结算
    private void settlement() {
    	goods_id = "";
    	String shop_id = "";
        String nums = "";
        int settlementNum = 0;
        for (int i = 0; i < carts.size(); i++) {
            Cart cart = carts.get(i);
            if (cart.isSelect()) {
                settlementNum++;
                if (settlementNum == 1) {
                	goods_id = cart.getGoods_id();
                	shop_id = cart.getId();
                    nums = String.valueOf(cart.getGoods_num());
                } else {
                	goods_id = goods_id + "," + cart.getGoods_id();
                	shop_id = shop_id + "," + cart.getId();
                    nums = nums + "," + cart.getGoods_num();
                }
            }
        }
        if (settlementNum < 1) {
            ToastUtil.getInstance().showToast(this, R.string.please_goods);
            return;
        }
        
        Intent intent = new Intent(context,
                OrderDetailActivity.class);
        intent.putExtra("goods_id", goods_id);
        intent.putExtra("shop_id", shop_id);
        intent.putExtra("goods_num", nums);
        intent.putExtra("isCar", true);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slider_right_in,
                R.anim.anim_slider_left_out);
        

//		Map<String, String> params = new HashMap<String, String>();
//		params.put("tocken", mToken);
//		params.put("app_nonce", app_nonce);
//		params.put("goods_id", goods_id);
//		params.put("shop_id", shop_id);
//		params.put("goods_num", nums);
//		LogUtils.e("购物车结算参数："+params.toString());
//		NetHelper.post(Constant.VIDEOCARDCART+"&act=makeoder", params,
//				new SimpleSingleBeanNetHandler<Captcha>(this) {
//
//					@Override
//					protected void onSuccess(Captcha bean) {
//						LogUtils.e("购物车结算：result=" + bean.result + "\ninfo="
//								+ bean.message);
//						if (bean.result.equals("0")) {
//							Intent intent = new Intent(context,
//	                                OrderDetailActivity.class);
//	                        intent.putExtra("ids", goods_id);
//	                        intent.putExtra("isCar", true);
//	                        startActivityForResult(intent, 0);
//        				overridePendingTransition(R.anim.anim_slider_right_in,R.anim.anim_slider_left_out);
//						} else {
//							Toast.makeText(context, bean.message + "",
//									Toast.LENGTH_SHORT).show();
//						}
//					}
//
//				});
	

    }
    
    
 // 获取tocken
 	private void getTokenLocal() {
 		app_nonce = StringUtil.getPhoneIMEI(this);
 		mToken = new TokenManager(this).getToken();
 		if (mToken != null) {
 			loading.setVisibility(View.VISIBLE);
 			iniData();
 		} else {
 			TokenManager.handlered = false;
 			Message msg_loop = handler.obtainMessage();
 			msg_loop.what = 2;
 			msg_loop.sendToTarget();
 		}

 	}

    /*
     * 购物车列表数据
     */
    private void iniData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		NetHelper.post(Constant.GETCART, params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("我的购物车列表：result=" + bean.result + "\ninfo="
								+ bean.info);
						if (bean.result.equals("0")) {
							String info = bean.info;
							try {
								JSONArray jsonArray = new JSONArray(info);
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject obj = (JSONObject) jsonArray.get(i);
									Cart videoCarInfo = GsonUtils.parseJSON(obj.toString(), Cart.class);
									if(videoCarInfo.getStatus().equals("1")){//1表示上架，0表示下架
										carts.add(videoCarInfo);
									}
								}
								if(carts.size() <= 0){
									tvNoData.setVisibility(View.VISIBLE);
									checkAll.setChecked(false);
								}else{
									tvNoData.setVisibility(View.GONE);
								}
								adapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
						loading.setVisibility(View.GONE);
					}

				});
	}
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            carts.clear();
            getTokenLocal();
            checkAll.setChecked(false);
            tvSum.setText("0.00");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    
    
    
    
    
    
    private class CartClickListener implements OnClickListener {

        private int position;
        private List<Cart> carts;

        private CartClickListener(List<Cart> carts, int position) {
            this.position = position;
            this.carts = carts;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.shoppingCarItem_radioBtn_select:
                CheckBox checkBox = (CheckBox) v;
                LogUtils.e("isChecked", checkBox.isChecked() + "  isChecked"
                        + position);
                if (checkBox.isChecked()) {
                    carts.get(position).setSelect(true);
                } else {
                    carts.get(position).setSelect(false);
                }
                Message msg = handler.obtainMessage();
                msg.what = CLICK_CHECKBOX;
                handler.sendMessage(msg);
                break;
            case R.id.shoppingCarItem_iv_del:
                deleteDialog(carts.get(position).getId(), position);
                break;
            case R.id.shoppingCard_ll_down:
                int number = carts.get(position).getGoods_num();
                if (number > 1) {
                    number--;

                    final Dialog dialog = LoadingDialogShow.loading(ShoppingCarActivity.this);
                    String token = (String) SPUtils.get(context, "token", "");
                    
                    Map<String, String> params = new HashMap<String, String>();
            		params.put("tocken", mToken);
            		params.put("app_nonce", app_nonce);
            		params.put("goods_id", carts.get(position).getGoods_id());
            		params.put("type", "subtract");
            		LogUtils.e("减少数量参数："+params.toString());
            		NetHelper.post(Constant.GETCARTNUM, params,
            				new SimpleSingleBeanNetHandler<Captcha>(ShoppingCarActivity.this) {

            					@Override
            					protected void onSuccess(Captcha bean) {
            						LogUtils.e("减少数量操作：result=" + bean.result + "\ninfo="
            								+ bean.message);
            						if (bean.result.equals("0")) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(bean.info);
                                            String goods_num = jsonObject.getString("goods_num");
                                                if (isNumeric(goods_num)) {
                                                	int num = Integer.valueOf(goods_num);
                                                    carts.get(position).setGoods_num(num);
                                                }
                                            if (dialog != null) {
                                                dialog.dismiss();
                                            }
                                            adapter.notifyDataSetChanged();
                                            Message msg = handler.obtainMessage();
                                            msg.what = CLICK_CHECKBOX;
                                            handler.sendMessage(msg);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
            							Toast.makeText(context, bean.message + "",
            									Toast.LENGTH_SHORT).show();
            						}
            					}

            				});
                    
                    
                }
                break;
            case R.id.shoppingCard_ll_up:
                number = carts.get(position).getGoods_num();
                number++;
                final Dialog dialog = LoadingDialogShow.loading(ShoppingCarActivity.this);
                String token = (String) SPUtils.get(context, "token", "");
                
                Map<String, String> params = new HashMap<String, String>();
        		params.put("tocken", mToken);
        		params.put("app_nonce", app_nonce);
        		params.put("goods_id", carts.get(position).getGoods_id());
        		params.put("type", "add");
        		LogUtils.e("增加数量参数："+params.toString());
        		NetHelper.post(Constant.GETCARTNUM, params,
        				new SimpleSingleBeanNetHandler<Captcha>(ShoppingCarActivity.this) {

        					@Override
        					protected void onSuccess(Captcha bean) {
        						LogUtils.e("增加数量操作：result=" + bean.result + "\ninfo="
        								+ bean.message);
        						if (bean.result.equals("0")) {
                                    try {
                                    	JSONObject jsonObject = new JSONObject(bean.info);
                                        String goods_num = jsonObject.getString("goods_num");
                                            if (isNumeric(goods_num)) {
                                            	int num = Integer.valueOf(goods_num);
                                                carts.get(position).setGoods_num(num);
                                            }
                                        if (dialog != null) {
                                            dialog.dismiss();
                                        }
                                        adapter.notifyDataSetChanged();
                                        Message msg = handler.obtainMessage();
                                        msg.what = CLICK_CHECKBOX;
                                        handler.sendMessage(msg);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
        							Toast.makeText(context, bean.message + "",
        									Toast.LENGTH_SHORT).show();
        						}
        					}

        				});
                break;
            default:
                break;
            }
        }
    }
    
    
 // 删除提示
    private void deleteDialog(final String cartItemId, final int position) {
    	final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(this);
		dailog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						delete(cartItemId, position);
						dialog.dismiss();
					}
				});

		dailog.setNagetiveButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();

					}
				});

		MyDialog creatDialog = dailog.creatDialog();
		View layout = dailog.getView();
		TextView tvTitle = (TextView) layout
				.findViewById(R.id.pop_title);
		tvTitle.setText("是否删除该商品？");
		EditText et = (EditText) layout
				.findViewById(R.id.ed_input);
		et.setVisibility(View.GONE);
		creatDialog.show();
    	
    }

    // 删除
    private void delete(String cartItemId, final int position) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("goods_id", carts.get(position).getGoods_id());
		NetHelper.post(Constant.VIDEOCARDCART+"&act=del", params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("我的购物车列表：result=" + bean.result + "\ninfo="
								+ bean.info);
						if (bean.result.equals("0")) {
							carts.remove(position);
	                        adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
						loading.setVisibility(View.GONE);
					}

				});
	
    	
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
