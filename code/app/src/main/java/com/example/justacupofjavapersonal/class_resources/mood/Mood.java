package com.example.justacupofjavapersonal.class_resources.mood;

import android.location.Location;

import com.example.justacupofjavapersonal.class_resources.User;

import java.util.Date;

/**
 * Represents a Mood event with details such as emotional state, social situations, location, and optional photo.
 */
public class Mood {

    private String moodID; // Immutable: Unique ID for the mood event
    private String privacy;
    private String uid; // Mutable: Unique username, with setter
    private Date postDate; // Immutable: Date and time of the mood event
    private String photo; // Optional, Base64-encoded string
    private EmotionalState state; // Mutable to allow corrections
    private String emotion;
    private String socialSituation; // Optional
    private Location location; // Optional
    private String whyFeel; // Added to store the "Reason? max 200 characters" input

    private String date;
    private String time;

    private boolean hasPhoto = false;
    private boolean hasLocation = false;
    private boolean hasSocialSituation = false;

    /**
     * Constructs a Mood object with mandatory fields.
     *
     * @param state    the emotional state of the user
     * @param postDate the date and time the mood was posted
     * @throws IllegalArgumentException if any mandatory field is null
     */
    public Mood(EmotionalState state, Date postDate) {
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
        this.photo = null;
        this.socialSituation = null;
        this.location = null;
        this.whyFeel = null; // Initialize whyFeel
    }

    /**
     * Constructs a Mood object with both mandatory and optional fields.
     *
     * @param state           the emotional state of the user
     * @param postDate        the date and time the mood was posted
     * @param photo           a photo associated with the mood (optional, Base64-encoded string)
     * @param socialSituation the social situation when the mood was recorded (optional)
     * @param location        the location of the mood event (optional)
     */
    public Mood(EmotionalState state, Date postDate, String photo, String socialSituation, Location location) {
        this(state, postDate);
        this.setPhoto(photo);
        this.setSocialSituation(socialSituation);
        this.setLocation(location);
    }

    public Mood() {
        // Empty constructor for Firebase
    }

    /**
     * Gets the social situation of the mood event.
     *
     * @return the social situation or null if not set
     */
    public String getSocialSituation() {
        return this.socialSituation;
    }

    /**
     * Sets the social situation of the mood event.
     *
     * @param socialSituation the social situation to set
     */
    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
        this.hasSocialSituation = socialSituation != null;
    }

    /**
     * Gets the emotional state of the mood event.
     *
     * @return the emotional state
     */
    public EmotionalState getState() {
        return state;
    }

    /**
     * Sets the emotional state of the mood event.
     *
     * @param state the emotional state to set
     */
    public void setState(EmotionalState state) {
        this.state = state;
    }

    /**
     * Gets the post date of the mood event.
     *
     * @return the post date
     */
    public Date getPostDate() {
        return postDate;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the user ID
     */
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets the unique mood ID.
     *
     * @return the mood ID
     */
    public String getMoodID() {
        return moodID;
    }

    /**
     * Gets the photo associated with the mood event.
     *
     * @return the photo as a Base64-encoded string or null if not set
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Sets the photo associated with the mood event.
     *
     * @param photo the photo to set (Base64-encoded string)
     */
    public void setPhoto(String photo) {
        this.photo = photo;
        this.hasPhoto = photo != null;
    }

    /**
     * Gets the location of the mood event.
     *
     * @return the location or null if not set
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location of the mood event.
     *
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
        this.hasLocation = location != null;
    }

    /**
     * Gets the reason (whyFeel) for the mood event.
     *
     * @return the reason or null if not set
     */
    public String getWhyFeel() {
        return whyFeel;
    }

    /**
     * Sets the reason (whyFeel) for the mood event.
     *
     * @param whyFeel the reason to set
     */
    public void setWhyFeel(String whyFeel) {
        this.whyFeel = whyFeel;
    }

    /**
     * Checks if a photo is associated with the mood event.
     *
     * @return true if a photo is present, false otherwise
     */
    public boolean hasPhoto() {
        return hasPhoto;
    }

    /**
     * Checks if a location is associated with the mood event.
     *
     * @return true if a location is present, false otherwise
     */
    public boolean hasLocation() {
        return hasLocation;
    }

    /**
     * Checks if a social situation is associated with the mood event.
     *
     * @return true if a social situation is present, false otherwise
     */
    public boolean hasSocialSituation() {
        return hasSocialSituation;
    }

    /**
     * Returns a string representation of the Mood object, excluding the photo for privacy.
     *
     * @return a string representation of the Mood object
     */
    public String getEmotion() {
        return emotion;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "moodID=" + moodID +
                ", uid='" + uid + '\'' +
                ", postDate=" + postDate +
                ", socialSituation=" + (socialSituation == null ? "None" : socialSituation) +
                ", photo=" + (photo == null ? "None" : "Available") +
                ", location=" + (location == null ? "None" : location.toString()) +
                '}';
    }
}
