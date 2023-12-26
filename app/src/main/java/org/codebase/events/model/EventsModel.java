package org.codebase.events.model;

import android.graphics.drawable.Drawable;

public class EventsModel {
    int id;
    int userImage;
    String name;
    int eventImage;
    String eventTopic;

    public EventsModel(int id, int userImage, String name, int eventImage, String eventTopic) {
        this.id = id;
        this.userImage = userImage;
        this.name = name;
        this.eventImage = eventImage;
        this.eventTopic = eventTopic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEventImage() {
        return eventImage;
    }

    public void setEventImage(int eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventTopic() {
        return eventTopic;
    }

    public void setEventTopic(String eventTopic) {
        this.eventTopic = eventTopic;
    }
}
