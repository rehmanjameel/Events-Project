package pk.cust.events.model;

public class EventsModel {
    int id;
    int eventImage;
    String eventTitle;

    public EventsModel(int id, int eventImage, String eventTitle) {
        this.id = id;
        this.eventImage = eventImage;
        this.eventTitle = eventTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventImage() {
        return eventImage;
    }

    public void setEventImage(int eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
