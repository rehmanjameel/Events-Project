package pk.cust.events.model;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomeEventsModel {
    private String userId;
    private String postId;
    private String userName;
    private String userImage;
    private String description;
    private String imageUrl;
    private String domain;
    private long startDateTime;
    private long endDateTime;


//    public HomeEventsModel(String userId, String userName, String userImage, String description,
//                           String imageUrl, int likesCount, List<String> likedBy) {
//        this.userId = userId;
//        this.userName = userName;
//        this.userImage = userImage;
//        this.description = description;
//        this.imageUrl = imageUrl;
//        this.likesCount = likesCount;
//        this.likedBy = likedBy;
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(long endDateTime) {
        this.endDateTime = endDateTime;
    }
}
