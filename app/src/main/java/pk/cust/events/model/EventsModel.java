package pk.cust.events.model;

public class EventsModel {
    String id;
    String personName;
    String personImage;
    String eventImage;
    String eventTitle;
    String eventDomain;
    String postId;
    long startDateTime;
    String endDateTime;

    public EventsModel(String id, String personName, String personImage, String eventImage,
                       String eventTitle, String eventDomain, String postId, long startDateTime, String endDateTime) {
        this.id = id;
        this.personName = personName;
        this.personImage = personImage;
        this.eventImage = eventImage;
        this.eventTitle = eventTitle;
        this.eventDomain = eventDomain;
        this.postId = postId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDomain() {
        return eventDomain;
    }

    public void setEventDomain(String eventDomain) {
        this.eventDomain = eventDomain;
    }

    public String getPersonImage() {
        return personImage;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }
}
