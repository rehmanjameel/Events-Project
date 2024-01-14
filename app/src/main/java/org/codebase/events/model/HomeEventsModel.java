package org.codebase.events.model;

public class HomeEventsModel {
    private String userId;
    private String userName;  // You can include the user's name in the Post model for convenience
    private String userImage; // You can include the user's image URL in the Post model for convenience
    private String description;
    private String imageUrl;

    public HomeEventsModel(String userId, String userName, String userImage, String description, String imageUrl) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
