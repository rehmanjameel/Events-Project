package pk.cust.events.model;

public class EventsModel {
    String id;
    String eventImage;
    String eventTitle;

    public EventsModel(String id, String eventImage, String eventTitle) {
        this.id = id;
        this.eventImage = eventImage;
        this.eventTitle = eventTitle;
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
}
