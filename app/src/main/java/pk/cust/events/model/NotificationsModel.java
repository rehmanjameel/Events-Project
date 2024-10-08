package pk.cust.events.model;

public class NotificationsModel {
    String id;
    String name;
    String description;
    String chatId;
    String postId;
    boolean rejectButtonHidden; // New field to track reject button visibility

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isRejectButtonHidden() {
        return rejectButtonHidden;
    }

    public void setRejectButtonHidden(boolean rejectButtonHidden) {
        this.rejectButtonHidden = rejectButtonHidden;
    }
}
