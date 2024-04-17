package pk.cust.events.model;

public class FriendsModel {
    String id;
    String friendImage;
    String friendName;
    String friendDomain;
    String friendInterest;
    String friendEmail;
    String friendPhoneNo;
    String friendDob;
    String friendAddress;
    String friendGender;

    public FriendsModel(String id, String friendImage, String friendName, String friendDomain,
                        String friendInterest, String friendEmail, String friendPhoneNo,
                        String friendDob, String friendAddress, String friendGender) {
        this.id = id;
        this.friendImage = friendImage;
        this.friendName = friendName;
        this.friendDomain = friendDomain;
        this.friendInterest = friendInterest;
        this.friendEmail = friendEmail;
        this.friendPhoneNo = friendPhoneNo;
        this.friendDob = friendDob;
        this.friendAddress = friendAddress;
        this.friendGender = friendGender;
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

    public String getFriendInterest() {
        return friendInterest;
    }

    public void setFriendInterest(String friendInterest) {
        this.friendInterest = friendInterest;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public String getFriendPhoneNo() {
        return friendPhoneNo;
    }

    public void setFriendPhoneNo(String friendPhoneNo) {
        this.friendPhoneNo = friendPhoneNo;
    }

    public String getFriendDob() {
        return friendDob;
    }

    public void setFriendDob(String friendDob) {
        this.friendDob = friendDob;
    }

    public String getFriendAddress() {
        return friendAddress;
    }

    public void setFriendAddress(String friendAddress) {
        this.friendAddress = friendAddress;
    }

    public String getFriendGender() {
        return friendGender;
    }

    public void setFriendGender(String friendGender) {
        this.friendGender = friendGender;
    }
}
