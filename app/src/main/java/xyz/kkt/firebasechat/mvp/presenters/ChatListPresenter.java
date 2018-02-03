package xyz.kkt.firebasechat.mvp.presenters;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import xyz.kkt.firebasechat.data.models.FireChatModel;
import xyz.kkt.firebasechat.data.vo.UserVO;
import xyz.kkt.firebasechat.delegates.ChatListDelegate;
import xyz.kkt.firebasechat.mvp.views.ChatListView;

public class ChatListPresenter extends BasePresenter<ChatListView> implements ChatListDelegate {

    private UserVO mPartner;


    public ChatListPresenter() {
    }

    @Override
    public void onCreate(ChatListView view) {
        super.onCreate(view);
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

//    public void onTapConversation(String partnerId) {
//
//        mPartnerId = partnerId;
//
//        if (FireChatModel.getInstance().isUserAuthenticate()) {
//            mView.navigateToConversationScreen(partnerId);
//        } else {
//            mView.showAuthenticationDialog();
//        }
//    }

    public void onSuccessGoogleSignIn(GoogleSignInAccount signInAccount) {
        FireChatModel.getInstance().authenticateUserWithGoogleAccount(signInAccount, new FireChatModel.UserAuthenticateDelegate() {
            @Override
            public void onSuccessAuthenticate(GoogleSignInAccount signInAccount) {
                mView.navigateToConversationScreen(mPartner);
            }

            @Override
            public void onFailureAuthenticate(String errrorMsg) {
                mView.showErrorInfo(errrorMsg);
            }
        });
    }

    @Override
    public void onTapConversation(UserVO partner) {
        partner = partner;

        if (FireChatModel.getInstance().isUserAuthenticate()) {
            mView.navigateToConversationScreen(partner);
        } else {
            mView.showAuthenticationDialog();
        }
    }
}
