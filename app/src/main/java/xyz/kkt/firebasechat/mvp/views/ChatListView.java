package xyz.kkt.firebasechat.mvp.views;

import android.content.Context;

import java.util.List;

import xyz.kkt.firebasechat.data.vo.ChatVO;
import xyz.kkt.firebasechat.data.vo.UserVO;


public interface ChatListView {

    void displayChatList(List<ChatVO> chatList);

    void showLoading();

    Context getContext();

    void navigateToConversationScreen(UserVO partner);

    void showAuthenticationDialog();

    void showErrorInfo(String message);
}
