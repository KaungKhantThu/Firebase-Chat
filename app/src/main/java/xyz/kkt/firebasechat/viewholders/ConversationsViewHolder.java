package xyz.kkt.firebasechat.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.kkt.firebasechat.R;
import xyz.kkt.firebasechat.data.vo.ConversationVO;

/**
 * Created by Dell on 2/3/2018.
 */

public class ConversationsViewHolder extends BaseViewHolder<ConversationVO> {

    @BindView(R.id.iv_sent_photo)
    ImageView ivSentPhoto;

    @BindView(R.id.iv_me)
    CircleImageView ivSender;

    @BindView(R.id.tv_text)
    TextView tvMessage;

    ConversationVO mData;

    public ConversationsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setData(ConversationVO mData) {
        this.mData = mData;
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop();
        if(!mData.getPhotoMsg().equals("")){
            ivSentPhoto.setVisibility(View.VISIBLE);
            Glide.with(itemView.getRootView().getContext()).load(mData.getPhotoMsg()).apply(requestOptions).into(ivSentPhoto);
        }else {
            ivSentPhoto.setVisibility(View.GONE);
        }
        if(!mData.getMsg().equals("")){
            tvMessage.setText(mData.getMsg());
        }else {
            tvMessage.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {

    }
}
