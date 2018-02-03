package xyz.kkt.firebasechat.delegates;

import xyz.kkt.firebasechat.data.vo.UserVO;

/**
 * Created by Lenovo on 2/3/2018.
 */

public interface ChatListDelegate {
    void onTapConversation(UserVO partner);
}
