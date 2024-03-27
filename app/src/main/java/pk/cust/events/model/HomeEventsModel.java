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
    private int likesCount;
    private List<String> likedBy;


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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public boolean isLiked(String currentUserId) {
        return likedBy != null && likedBy.contains(currentUserId);
    }
}
