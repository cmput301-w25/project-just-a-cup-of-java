package com.example.just_a_cup_of_java;

import android.location.Location;

import java.util.Date;

public class Mood {

    private Integer moodID; // Immutable: Unique ID for the mood event
    private String username; // Mutable: Unique username, with setter
    private Date postDate; // Immutable: Date and time of the mood event
    private String trigger; //max 20 characters and is optional
    private byte[] photo; //optional
    private EmotionalState state; //Mutable inorder to provide the user a chance to correct a mistake
    private SocialSituation socialSituation; //optional
    private Location location; //optional


    public Mood(Integer moodID, String username, EmotionalState state, Date postDate) {
        this.moodID = moodID;
        this.username = username;
        this.state = state;
        this.postDate = postDate;
    }

    public SocialSituation getSocialSituation() {
        return this.socialSituation;
    }

    public void setSocialSituation(SocialSituation socialSituation) {
        this.socialSituation = socialSituation;
    }

    public EmotionalState getState() {
        return state;
    }

    public void setState(EmotionalState state) {
        this.state = state;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {

        if(trigger.length() > 20){
            throw new IllegalArgumentException("Trigger must be 20 characters at most.");
        }
        this.trigger = trigger;
    }



    public Date getPostDate() {
        return postDate;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMoodID() {
        return moodID;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        if (photo != null && photo.length > 65536) {
            throw new IllegalArgumentException("Photo must be under 65536 bytes.");
        }
        this.photo = photo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
