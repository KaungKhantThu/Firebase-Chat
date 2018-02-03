package xyz.kkt.firebasechat.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.kkt.firebasechat.R;
import xyz.kkt.firebasechat.adapters.ConversationsAdapter;
import xyz.kkt.firebasechat.components.SmartRecyclerView;
import xyz.kkt.firebasechat.data.models.FireChatModel;
import xyz.kkt.firebasechat.events.RestApiEvents;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_IMAGE_REQUEST = 1;

    public static final String PARTNER_ID = "PARTNER_ID";
    public static final String PARTNER_NAME = "PARTNER_NAME";
    public static final String PARTNER_PROFILE_IMAGE = "PARTNER_PROFILE_IMAGE";

    @BindView(R.id.iv_add_photo)
    ImageView ivAddPhoto;

    @BindView(R.id.iv_selected_photo)
    ImageView ivSelectedPhoto;

    @BindView(R.id.iv_close)
    ImageView ivClose;

    @BindView(R.id.rl_selected_photo)
    RelativeLayout rlSelectedPhoto;

    @BindView(R.id.iv_send)
    ImageView ivSend;

    @BindView(R.id.et_message)
    EditText etMessage;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.rv_conversations)
    SmartRecyclerView rvConversations;

    @BindView(R.id.iv_partner)
    ImageView ivPartner;

    @BindView(R.id.tv_partner_name)
    TextView tvPartnerName;

    private String mPartnerId;
    private String mPhotoUrl;

    private ConversationsAdapter mConversationAdapter;

    public static Intent newIntent(Context context, String partnerId, String name, String profileUrl) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(PARTNER_ID, partnerId);
        intent.putExtra(PARTNER_NAME, name);
        intent.putExtra(PARTNER_PROFILE_IMAGE, profileUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getIntent().getStringExtra(PARTNER_ID).isEmpty()) {
            mPartnerId = getIntent().getStringExtra(PARTNER_ID);
        }
        if (!getIntent().getStringExtra(PARTNER_NAME).isEmpty()) {
            tvPartnerName.setText(getIntent().getStringExtra(PARTNER_NAME));
        }
        if (!getIntent().getStringExtra(PARTNER_PROFILE_IMAGE).isEmpty()) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.mipmap.ic_launcher)
                    .centerCrop();
            Glide.with(getApplicationContext()).load(getIntent().getStringExtra(PARTNER_PROFILE_IMAGE)).apply(requestOptions).into(ivPartner);
        }

        swipeRefreshLayout.setRefreshing(true);
        FireChatModel.getInstance().startConversation(mPartnerId);

        mConversationAdapter = new ConversationsAdapter(getApplicationContext());
        rvConversations.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvConversations.setLayoutManager(layoutManager);
        rvConversations.setAdapter(mConversationAdapter);

        ivAddPhoto.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ivSend.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onConversationLoaded(RestApiEvents.ChatDataLoadedEvent event) {
        mConversationAdapter.setNewData(event.getLoadedConversations());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            Uri uri = data.getData();
            onPhotoTaken(uri.toString());
            //mTakenPhotosAdapter.addNewData(uri.toString());
        }
    }

    private void onPhotoTaken(String photoUrl) {
        mPhotoUrl = photoUrl;
        if (TextUtils.isEmpty(photoUrl)) {
            Snackbar.make(ivAddPhoto, "ERROR : Path to photo is empty.", Snackbar.LENGTH_LONG).show();
            rlSelectedPhoto.setVisibility(View.GONE);
        } else {
            rlSelectedPhoto.setVisibility(View.VISIBLE);
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.mipmap.ic_launcher)
                    .centerCrop();
            Glide.with(getApplicationContext()).load(photoUrl).apply(requestOptions).into(ivSelectedPhoto);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_send:
                String message = etMessage.getText().toString();
                if (mPhotoUrl == null && message.equals("")) {
                    Snackbar.make(rvConversations, "Write a message", Snackbar.LENGTH_INDEFINITE).show();
                } else {
                    swipeRefreshLayout.setRefreshing(true);
                    FireChatModel.getInstance().sendMessage(mPhotoUrl, message);
                }
                rlSelectedPhoto.setVisibility(View.GONE);
                etMessage.setText("");
                mPhotoUrl = null;
                break;
            case R.id.iv_close:
                mPhotoUrl = null;
                rlSelectedPhoto.setVisibility(View.GONE);
                break;
            case R.id.iv_add_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;
        }
    }
}
