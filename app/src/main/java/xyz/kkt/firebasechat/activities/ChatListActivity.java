package xyz.kkt.firebasechat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.kkt.firebasechat.FireChatApp;
import xyz.kkt.firebasechat.R;
import xyz.kkt.firebasechat.adapters.ChatListAdapter;
import xyz.kkt.firebasechat.components.EmptyViewPod;
import xyz.kkt.firebasechat.components.SmartRecyclerView;
import xyz.kkt.firebasechat.components.SmartScrollListener;
import xyz.kkt.firebasechat.data.models.FireChatModel;
import xyz.kkt.firebasechat.data.vo.ChatVO;
import xyz.kkt.firebasechat.data.vo.UserVO;
import xyz.kkt.firebasechat.dialogs.SignInDialog;
import xyz.kkt.firebasechat.events.RestApiEvents;
import xyz.kkt.firebasechat.mvp.presenters.ChatListPresenter;
import xyz.kkt.firebasechat.mvp.views.ChatListView;

public class ChatListActivity extends BaseActivity implements ChatListView,
        GoogleApiClient.OnConnectionFailedListener {

    protected static final int RC_GOOGLE_SIGN_IN = 1236;

    @BindView(R.id.rv_chat_list)
    SmartRecyclerView rvChatList;

    @BindView(R.id.vp_empty)
    EmptyViewPod vpEmpty;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private SmartScrollListener mSmartScrollListener;

    private ChatListAdapter mChatListAdapter;

    protected GoogleApiClient mGoogleApiClient;

    private ChatListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPresenter = new ChatListPresenter();

        mPresenter.onCreate(this);


        rvChatList.setEmptyView(vpEmpty);
        rvChatList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mChatListAdapter = new ChatListAdapter(getApplicationContext(), mPresenter);
        rvChatList.setAdapter(mChatListAdapter);
        rvChatList.setHasFixedSize(true);

        mSmartScrollListener = new SmartScrollListener(new SmartScrollListener.OnSmartScrollListener() {
            @Override
            public void onListEndReached() {

            }
        });
        rvChatList.addOnScrollListener(mSmartScrollListener);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("789363895517-q39lql12hhhd7j6qd4rgkarg3e7o44a1.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /*FragmentActivity*/, this /*OnConnectionFailedListener*/)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            processGoogleSignInResult(result);
        }
    }

    private void processGoogleSignInResult(GoogleSignInResult signInResult) {
        if (signInResult.isSuccess()) {
            // Google Sign-In was successful, authenticate with Firebase
            GoogleSignInAccount account = signInResult.getSignInAccount();
            mPresenter.onSuccessGoogleSignIn(account);
        } else {
            // Google Sign-In failed
            Log.e(FireChatApp.LOG, "Google Sign-In failed.");
            Snackbar.make(rvChatList, "Your Google sign-in failed.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatListDataLoaded(RestApiEvents.UserDataLoadedEvent event) {
        mChatListAdapter.appendNewData(event.getLoadedUser());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (FireChatModel.getInstance().getmUserList() != null && FireChatModel.getInstance().getmUserList().size() > 0) {
            mChatListAdapter.appendNewData(FireChatModel.getInstance().getmUserList());
        } else {
            swipeRefreshLayout.setRefreshing(true);
            FireChatModel.getInstance().startLoadingRegisteredUser(getApplicationContext());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayChatList(List<ChatVO> chatList) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void navigateToConversationScreen(UserVO partner) {

        Intent intent = ConversationActivity.newIntent(getApplicationContext(), partner.getUserId(), partner.getName(), partner.getProfileUrl());
        startActivity(intent);
    }

    @Override
    public void showAuthenticationDialog() {
        SignInDialog dialog = new SignInDialog();
        dialog.show(getSupportFragmentManager(), "Authenticate");
        dialog.setTapListener(new SignInDialog.TapListener() {
            @Override
            public void onTapAgree() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            }
        });
    }

    @Override
    public void showErrorInfo(String message) {
        Snackbar.make(rvChatList, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }
}
