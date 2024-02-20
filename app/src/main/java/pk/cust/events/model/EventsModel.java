package pk.cust.events.model;

public class EventsModel {
    String id;
    String personName;
    String personImage;
    String eventImage;
    String eventTitle;
    String eventDomain;

    public EventsModel(String id, String personName, String personImage, String eventImage, String eventTitle, String eventDomain) {
        this.id = id;
        this.personName = personName;
        this.personImage = personImage;
        this.eventImage = eventImage;
        this.eventTitle = eventTitle;
        this.eventDomain = eventDomain;
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
}
