package com.example.just_a_cup_of_java;

import com.example.just_a_cup_of_java.User;

public abstract class Mood {
    private String location = "";
    private String trigger = "";
    private String situation = "";
    private String reason = "";
    private String photoURL = "";

    private User user = null;

    public abstract String getMoodType();
    public abstract String getMoodImage();
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getTrigger() {
        return trigger;
    }
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
    public String getSituation() {
        return situation;
    }
    public void setSituation(String situation) {
        this.situation = situation;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getPhotoURL() {
        return photoURL;
    }
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}

class HappyMood extends Mood {
    public HappyMood() {
        this.emotionalState = "Happy";
    }

    @Override
    public String getMoodType() {
        return "Happy";
    }
}

// add more later