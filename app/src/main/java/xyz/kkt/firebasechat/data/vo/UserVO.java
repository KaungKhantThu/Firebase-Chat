package xyz.kkt.firebasechat.data.vo;


public class UserVO {

    private String coverUrl;
    private String email;
    private String name;
    private String profileUrl;
    private String userId;

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
