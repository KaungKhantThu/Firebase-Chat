package xyz.kkt.firebasechat.events;

import java.util.List;

import xyz.kkt.firebasechat.data.vo.ChatVO;
import xyz.kkt.firebasechat.data.vo.ConversationVO;
import xyz.kkt.firebasechat.data.vo.UserVO;

public class RestApiEvents {
    public static class EmptyResponseEvent {

    }

    public static class ErrorInvokingAPIEvent {
        private String errorMsg;

        public ErrorInvokingAPIEvent(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    public static class ChatDataLoadedEvent {
        private List<ConversationVO> loadedConversations;

        public ChatDataLoadedEvent(List<ConversationVO> loadedConversations) {
            this.loadedConversations = loadedConversations;
        }

        public List<ConversationVO> getLoadedConversations() {
            return loadedConversations;
        }
    }

    public static class UserDataLoadedEvent {
        private List<UserVO> loadedUser;

        public UserDataLoadedEvent(List<UserVO> loadedUser) {
            this.loadedUser = loadedUser;
        }

        public List<UserVO> getLoadedUser() {
            return loadedUser;
        }
    }
}
