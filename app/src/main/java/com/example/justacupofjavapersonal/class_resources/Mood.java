package com.example.justacupofjavapersonal.class_resources;

import android.location.Location;

import java.util.Date;

public class Mood {

    private final String moodID; // Immutable: Unique ID for the mood event
    private String uid; // Mutable: Unique username, with setter
    private final Date postDate; // Immutable: Date and time of the mood event
    private String trigger; //max 20 characters and is optional
    private byte[] photo; //optional
    private EmotionalState state; //Mutable inorder to provide the user a chance to correct a mistake
    private SocialSituation socialSituation; //optional
    private Location location; //optional


    public Mood(String moodID, String uid, EmotionalState state, Date postDate) {
        if (moodID == null) {
            throw new IllegalArgumentException("Mood ID is required.");
        }
        if (uid == null) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (state == null) {
            throw new IllegalArgumentException("Emotional state is required.");
        }
        if (postDate == null) {
            throw new IllegalArgumentException("Post date is required.");
        }
        this.moodID = moodID;
        this.uid = uid;
        this.state = state;
        this.postDate = postDate;

        // Optional fields default to null
        this.trigger = null;
        this.photo = null;
        this.socialSituation = null;
        this.location = null;
    }

    public Mood(String moodID, String uid, Date postDate, String trigger, byte[] photo, EmotionalState state, SocialSituation socialSituation, Location location) {
        this.moodID = moodID;
        this.uid = uid;
        this.postDate = postDate;
        this.state = state;

        this.trigger = trigger;
        this.photo = photo;
        this.socialSituation = socialSituation;
        this.location = location;
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


    public String getUid() {
        return uid;
    }

    public String getMoodId() {
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

    @Override
    public String toString() { //useful for debugging purposes. Doesn't include photo
        return "Mood{" +
                "moodID=" + moodID +
                ", uid='" + uid + '\'' +
                ", postDate=" + postDate +
                ", trigger='" + (trigger == null ? "None" : trigger) + '\'' +
                ", emotionalState=" + state +
                ", socialSituation=" + (socialSituation == null ? "None" : socialSituation) +
                ", photo=" + (photo == null ? "None" : "Available") +
                ", location=" + (location == null ? "None" : location.toString()) +
                '}';
    }


}
