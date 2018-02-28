package org.soshow.common.adapter;

import java.util.List;

import org.soshow.beautyedu.R;

import android.content.Context;


/**
 * Created by zhy on 15/9/4.
 */
public class ChatAdapter extends MultiItemCommonAdapter<ChatMessage> {

    public final static int RECIEVE_MSG = 0;
    public final static int SEND_MSG = 1;

    public ChatAdapter(Context context, List<ChatMessage> datas) {
        super(context, datas, new MultiItemTypeSupport<ChatMessage>() {
            @Override
            public int getLayoutId(int position, ChatMessage msg) {
                if (msg.isComMeg()) {
                    return R.layout.main_chat_from_msg;
                }
                return R.layout.main_chat_send_msg;
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public int getItemViewType(int postion, ChatMessage msg) {
                if (msg.isComMeg()) {
                    return RECIEVE_MSG;
                }
                return SEND_MSG;
            }
        });
    }

    @Override
    public void convert(ViewHolder holder, ChatMessage chatMessage) {

        switch (holder.getLayoutId()) {
        case R.layout.main_chat_from_msg:
            holder.setText(R.id.chat_from_content, chatMessage.getContent());
            holder.setText(R.id.chat_from_name, chatMessage.getName());
            holder.setImageResource(R.id.chat_from_icon, chatMessage.getIcon());
            break;
        case R.layout.main_chat_send_msg:
            holder.setText(R.id.chat_send_content, chatMessage.getContent());
            holder.setText(R.id.chat_send_name, chatMessage.getName());
            holder.setImageResource(R.id.chat_send_icon, chatMessage.getIcon());
            break;
        }
    }
}
