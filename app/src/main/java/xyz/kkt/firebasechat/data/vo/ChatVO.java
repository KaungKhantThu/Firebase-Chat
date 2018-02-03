package xyz.kkt.firebasechat.data.vo;

import java.util.HashMap;
import java.util.Map;

public class ChatVO {

    private String chatId;
    private String startedAt;
    private Map<String,ConversationVO> conversations;
    private Map<String,UserVO> userList;

    public String getChatId() {
        return chatId;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public Map<String,ConversationVO> getConversations() {
        if(conversations==null){
            conversations=new HashMap<>();
        }
        return conversations;
    }

    public Map<String,UserVO> getUserList() {
        return userList;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public void setConversations(Map<String, ConversationVO> conversations) {
        this.conversations = conversations;
    }

    public void setUserList(Map<String, UserVO> userList) {
        this.userList = userList;
    }
}
