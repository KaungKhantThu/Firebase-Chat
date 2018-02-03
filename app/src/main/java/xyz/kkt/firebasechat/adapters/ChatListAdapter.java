package xyz.kkt.firebasechat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import xyz.kkt.firebasechat.R;
import xyz.kkt.firebasechat.data.vo.UserVO;
import xyz.kkt.firebasechat.delegates.ChatListDelegate;
import xyz.kkt.firebasechat.viewholders.BaseViewHolder;
import xyz.kkt.firebasechat.viewholders.ChatListViewHolder;


public class ChatListAdapter extends BaseAdapter<ChatListViewHolder, UserVO> {

    private ChatListDelegate mChatListDelegate;

    public ChatListAdapter(Context context, ChatListDelegate chatListDelegate) {
        super(context);
        mChatListDelegate=chatListDelegate;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.view_item_chat_list, parent, false);
        return new ChatListViewHolder(view,mChatListDelegate);
    }

}
