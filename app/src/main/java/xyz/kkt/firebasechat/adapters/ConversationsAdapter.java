package xyz.kkt.firebasechat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import xyz.kkt.firebasechat.R;
import xyz.kkt.firebasechat.data.vo.ConversationVO;
import xyz.kkt.firebasechat.viewholders.ConversationsViewHolder;


/**
 * Created by Dell on 2/3/2018.
 */

public class ConversationsAdapter extends BaseAdapter<ConversationsViewHolder, ConversationVO> {
    public ConversationsAdapter(Context context) {
        super(context);
    }

    @Override
    public ConversationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.view_item_chat_data, parent, false);
        return new ConversationsViewHolder(view);
    }
}
