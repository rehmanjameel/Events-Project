package pk.cust.events.model;

public class FriendsModel {
    String id;
    String friendImage;
    String friendName;
    String friendDomain;

    public FriendsModel(String id, String friendImage, String friendName, String friendDomain) {
        this.id = id;
        this.friendImage = friendImage;
        this.friendName = friendName;
        this.friendDomain = friendDomain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFriendImage() {
        return friendImage;
    }

    public void setFriendImage(String friendImage) {
        this.friendImage = friendImage;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendDomain() {
        return friendDomain;
    }

    public void setFriendDomain(String friendDomain) {
        this.friendDomain = friendDomain;
    }
}
