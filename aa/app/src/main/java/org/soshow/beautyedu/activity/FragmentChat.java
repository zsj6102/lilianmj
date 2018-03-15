package org.soshow.beautyedu.activity;

import java.util.List;
import java.util.Map;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMEventListener;

import com.easemob.EMNotifierEvent;


import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;

import com.easemob.chat.EMMessage;


import com.easemob.chatuidemo.DemoHelper;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.db.UserDao;

import com.easemob.chatuidemo.ui.AddContactActivity;
import com.easemob.chatuidemo.ui.ContactListFragment;
import com.easemob.chatuidemo.ui.ConversationListFragment;
import com.easemob.chatuidemo.ui.GroupsActivity;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.easemob.util.NetUtils;


/**
 * 好友聊天
 */
public class FragmentChat extends Fragment implements OnClickListener,  EMEventListener {
	// private SearchAdapter adapter;
	private Dialog loading;
	private TextView title_main;
	public static ImageView back_search;
	public static ImageView ivAdd;
	private SharedPreferences sp;
	public static List<Map<String, Object>> listItems;
	// 环信
	// 未读消息textview
	private static TextView unreadLabel;
	// 未读通讯录textview
	private TextView unreadAddressLable;

	private TextView[] tabs;
	private ContactListFragment contactListFragment;

	private ConversationListFragment chatHistoryFragment;
	private Fragment[] fragments;
	// 当前fragment的index
	private int currentTabIndex = 0;
	// 账号在别处登录
	public static boolean isConflict = false;
	// 账号被移除
	private static boolean isCurrentAccountRemoved = false;
	private boolean isAccountBlockedDialogShow;
	private String TAG = "FragmentChat";
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;
	public static boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_main, null);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
				false)) {
			Log.d("onCreateView", "1111111111111111");
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			DemoHelper.getInstance().logout(true, null);
			startActivity(new Intent(getActivity(), LoginInputActivity.class));
			getActivity().finish();
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			Log.d("onCreateView", "222222222222222");
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			getActivity().finish();
			startActivity(new Intent(getActivity(), LoginInputActivity.class));
		}
		initView(rootView);
		if (getActivity().getIntent().getBooleanExtra(com.easemob.chatuidemo.Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getActivity().getIntent().getBooleanExtra(com.easemob.chatuidemo.Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		} else if (getActivity().getIntent().getBooleanExtra(com.easemob.chatuidemo.Constant.ACCOUNT_BE_BLOCKED, false) && !isAccountBlockedDialogShow) {
			showAccountBlockedDialog();
		}


		inviteMessgeDao = new InviteMessgeDao(getActivity());
		userDao = new UserDao(getActivity());
		// 这个fragment只显示好友和群组的聊天记录
		// chatHistoryFragment = new ChatHistoryFragment();
		// 显示所有人消息记录的fragment


		chatHistoryFragment = new ConversationListFragment();
		contactListFragment = new ContactListFragment();

		fragments = new Fragment[] { chatHistoryFragment, contactListFragment };
		// 添加显示第一个fragment
		getActivity().getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, chatHistoryFragment).add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(chatHistoryFragment)
				.commit();
		/*
		 *
				.add(R.id.fragment_container, contactListFragment)
				.hide(contactListFragment).show(chatHistoryFragment).commit();
		 */
		init();
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}

		return rootView;

	}
	private void showAccountBlockedDialog(){
		isAccountBlockedDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String title = getString(R.string.user_be_blocked);
		String message = getString(R.string.user_blocked_hint);
		if (!getActivity().isFinishing()) {
			showAlertDialog(title, message);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// MyApplication.list_lecture = new ArrayList<LectureList>();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		// Log.d("123321", "onAttach");

		super.onAttach(activity);

	}

	@Override
	public void onStart() {
		loading = ProgressDialogUtil.createLoadingDialog(getActivity(), null,
				true, false);
		title_main = (TextView) getActivity().findViewById(R.id.title_main);
		title_main.setText("结界");
		back_search = (ImageView) getActivity().findViewById(R.id.back_search);
		back_search.setVisibility(View.INVISIBLE);
		back_search.setOnClickListener(this);
		ivAdd = (ImageView) getActivity().findViewById(R.id.add_frends);
		Log.e(TAG, "currentTabIndex=" + currentTabIndex);
		if(MainTabActivity.index == 0 || !MyApplication.getInstance().logined){
			ivAdd.setVisibility(View.GONE);
		}else if(MainTabActivity.index == 1 && MyApplication.getInstance().logined){
				ivAdd.setVisibility(View.VISIBLE);
		}
		super.onStart();
	}

	/*
	 * 环信
	 */
	private void init() {
		DemoHelper.getInstance().registerGroupAndContactListener();
		registerBroadcastReceiver();
	}
	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(com.easemob.chatuidemo.Constant.ACTION_CONTACT_CHANAGED);
		intentFilter.addAction(com.easemob.chatuidemo.Constant.ACTION_GROUP_CHANAGED);
		intentFilter.addAction(com.easemob.chatuidemo.Constant.MSG_RECEVIE);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateUnreadLabel();
				updateUnreadAddressLable();
				if (currentTabIndex == 0) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (chatHistoryFragment != null) {
						chatHistoryFragment.refresh();
							tabs[0].performClick();
					}

				} else if (currentTabIndex == 1) {
					if(contactListFragment != null) {
						contactListFragment.refresh();
							tabs[1].performClick();
					}

				}
				String action = intent.getAction();
				if(action.equals(com.easemob.chatuidemo.Constant.ACTION_GROUP_CHANAGED)){
					if (EaseCommonUtils.getTopActivity(getActivity()).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			}
		};
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}
	private void unregisterBroadcastReceiver(){
		broadcastManager.unregisterReceiver(broadcastReceiver);
	}
	private void showConflictDialog() {
		isConflictDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!getActivity().isFinishing()) {
			showAlertDialog(st, getString(R.string.connect_conflict));
			isConflict = true;
		}

	}
	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String title = getString(R.string.Remove_the_notification);
		String message = getString(R.string.em_user_remove);
		if (!getActivity().isFinishing()) {
			showAlertDialog(title, message);
			isCurrentAccountRemoved = true;
		}
	}
	private void showAlertDialog(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				getActivity().finish();
				startActivity(new Intent(getActivity(), LoginInputActivity.class));
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}
	/**
	 * 初始化组件
	 */
	private void initView(View view) {
		unreadLabel = (TextView) view.findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) view
				.findViewById(R.id.unread_address_number);
		tabs = new TextView[3];
		viewConversation = view.findViewById(R.id.view_conversation);
		viewConversation.setVisibility(View.VISIBLE);
		viewAddress = view.findViewById(R.id.view_address_list);
		viewAddress.setVisibility(View.INVISIBLE);
		tabs[0] = (TextView) view.findViewById(R.id.btn_conversation);
		tabs[1] = (TextView) view.findViewById(R.id.btn_address_list);
		tabs[2] = (TextView) view.findViewById(R.id.btn_setting);
		// 把第一个tab设为选中状态
		tabs[0].setTextColor(getActivity().getResources().getColor(R.color.bg_green));
		tabs[0].setOnClickListener(this);
		tabs[1].setOnClickListener(this);
		tabs[2].setOnClickListener(this);

		registerForContextMenu(tabs[1]);
	}

	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
			case EventNewMessage: // 普通消息
				EMMessage message = (EMMessage) event.getData();
				// 提示新消息
				DemoHelper.getInstance().getNotifier().onNewMsg(message);

				refreshUIWithMessage();
				break;
			case EventOfflineMessage: {
				refreshUIWithMessage();
				break;
			}

			case EventConversationListChanged: {
				refreshUIWithMessage();
				break;
			}
			case EventNewCMDMessage:
				EMMessage cmdMessage = (EMMessage) event.getData();
				//获取消息body
				CmdMessageBody cmdMsgBody = (CmdMessageBody) cmdMessage.getBody();
				final String action = cmdMsgBody.action;//获取自定义action
				if(action.equals(EaseConstant.EASE_ATTR_REVOKE)){
					EaseCommonUtils.receiveRevokeMessage(getActivity(), cmdMessage);
				}
				//red packet code : 处理红包回执透传消息

				refreshUIWithMessage();
				break;
			case EventReadAck:
				// TODO 这里当此消息未加载到内存中时，ackMessage会为null，消息的删除会失败
				EMMessage ackMessage = (EMMessage) event.getData();
				EMConversation conversation = EMChatManager.getInstance().getConversation(ackMessage.getTo());
				// 判断接收到ack的这条消息是不是阅后即焚的消息，如果是，则说明对方看过消息了，对方会销毁，这边也删除(现在只有txt iamge file三种消息支持 )
				if(ackMessage.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
						&& (ackMessage.getType() == EMMessage.Type.TXT
						|| ackMessage.getType() == EMMessage.Type.VOICE
						|| ackMessage.getType() == EMMessage.Type.IMAGE)){
					// 判断当前会话是不是只有一条消息，如果只有一条消息，并且这条消息也是阅后即焚类型，当对方阅读后，这边要删除，会话会被过滤掉，因此要加载上一条消息
					if(conversation.getAllMessages().size() == 1 && conversation.getLastMessage().getMsgId().equals(ackMessage.getMsgId())){
						if (ackMessage.getChatType() == EMMessage.ChatType.Chat) {
							conversation.loadMoreMsgFromDB(ackMessage.getMsgId(), 1);
						} else {
							conversation.loadMoreGroupMsgFromDB(ackMessage.getMsgId(), 1);
						}
					}
					conversation.removeMessage(ackMessage.getMsgId());
				}
				refreshUIWithMessage();
				break;
			default:
				break;
		}
	}
	private void refreshUIWithMessage() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
//				if (currentTabIndex == 0) {
//					// 当前页面如果为聊天历史页面，刷新此页面
//					// 当前页面如果为聊天历史页面，刷新此页面
//					if (chatHistoryFragment != null) {
//						chatHistoryFragment.refresh();
//					}
//				}
			}
		});
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterBroadcastReceiver();

	}
	public  void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
//					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
		return unreadAddressCountTotal;
	}
	/**
	 * 获取未读消息数
	 *
	 * @return
	 */
	public  int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for(EMConversation conversation:EMChatManager.getInstance().getAllConversations().values()){
			if(conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}



	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;



	@Override
	public void onResume() {
		super.onResume();
		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
		}
		// unregister this event listener when this activity enters the
		// background
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(getActivity());

		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged,
						EMNotifierEvent.Event.EventNewCMDMessage,
						EMNotifierEvent.Event.EventReadAck
				});

		// if push service available, connect will be disconnected after app in background
		// after activity restore to foreground, reconnect
		if (!EMChatManager.getInstance().isConnected() && NetUtils.hasNetwork(getActivity())) {
			EMChatManager.getInstance().reconnect();
		}
	}

	@Override
	public void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(getActivity());
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private View viewConversation;
	private View viewAddress;
	/**
	 * 帐号被移除的dialog
	 */

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction trx;
		switch (v.getId()) {
			case R.id.btn_conversation:// 聊天历史
				ivAdd.setVisibility(View.GONE);
				MainTabActivity.index = 0;
				tabs[0].setTextColor(getActivity().getResources().getColor(R.color.bg_green));
				viewConversation.setVisibility(View.VISIBLE);
				tabs[1].setTextColor(getActivity().getResources().getColor(R.color.text_gray));
				viewAddress.setVisibility(View.INVISIBLE);
				trx = getActivity().getSupportFragmentManager().beginTransaction();
				trx.hide(fragments[currentTabIndex]);
				if (!fragments[MainTabActivity.index].isAdded()) {
					trx.add(R.id.fragment_container, fragments[MainTabActivity.index]);
				}
				trx.show(fragments[MainTabActivity.index]).commitAllowingStateLoss();
				currentTabIndex = MainTabActivity.index;
				// activity.setVisity(false);
				break;
			case R.id.btn_address_list:// 联系人

				MainTabActivity.index = 1;
				tabs[0].setTextColor(getActivity().getResources().getColor(
						R.color.text_gray));
				viewConversation.setVisibility(View.INVISIBLE);
				tabs[1].setTextColor(getActivity().getResources().getColor(
						R.color.bg_green));
				viewAddress.setVisibility(View.VISIBLE);
				trx = getActivity().getSupportFragmentManager().beginTransaction();
				trx.hide(fragments[currentTabIndex]);
				if (!fragments[MainTabActivity.index].isAdded()) {
					trx.add(R.id.fragment_container, fragments[MainTabActivity.index]);
				}
				trx.show(fragments[MainTabActivity.index]).commitAllowingStateLoss();
				currentTabIndex = MainTabActivity.index;
				// activity.setVisity(true);

				// 添加好友
				if(MyApplication.logined == true){
					ivAdd.setVisibility(View.VISIBLE);
				}
				ivAdd.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(),
								AddContactActivity.class));
						getActivity().overridePendingTransition(R.anim.anim_slider_right_in,
								R.anim.anim_slider_left_out);
					}
				});
				break;
		}
	}
}
