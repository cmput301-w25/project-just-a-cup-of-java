package com.example.justacupofjavapersonal.class_resources.mood;

import android.location.Location;
import com.google.firebase.Timestamp;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import com.example.justacupofjavapersonal.class_resources.User;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.util.Date;
import java.util.Locale;

/**
 * Represents a Mood event with details such as emotional state, triggers,
 * social situations, location, and optional photo.
 */
@IgnoreExtraProperties

public class Mood implements Serializable {

    private String moodID; // Immutable: Unique ID for the mood event
    private String privacy;
    private Timestamp timestamp;


    //private Timestamp timestamp; // this will be used when querying the database. Allows us to sort

    private String uid; // Mutable: Unique username, with setter
    private Date postDate; // Immutable: Date and time of the mood event
    private String trigger; // Max 20 characters, optional
    //private Timestamp postDate;

    private byte[] photo; // Optional
    private EmotionalState state; // Mutable to allow corrections
    private String emotion;
    private String socialSituation; // Optional
    private Location location; // Optional

    private String date;
    private String time;

    private boolean hasPhoto = false;
    private boolean hasLocation = false;
    private boolean hasSocialSituation = false;
    private boolean hasTrigger = false;

    public transient boolean isDeserializing = false; // Transient flag to skip updateTimestamp during deserialization - I touched this

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
        this.trigger = null;
        this.photo = null;
        this.socialSituation = null;
        this.location = null;
    }

    /**
     * Constructs a Mood object with both mandatory and optional fields.
     *
     * @param state           the emotional state of the user
     * @param postDate        the date and time the mood was posted
     * @param trigger         the trigger for the mood (optional, max 20 characters)
     * @param photo           a photo associated with the mood (optional, max 65536 bytes)
     * @param socialSituation the social situation when the mood was recorded (optional)
     * @param location        the location of the mood event (optional)
     */
    public Mood(EmotionalState state, Date postDate,
                String trigger, byte[] photo, String socialSituation, Location location) {
        this(state, postDate);
        this.setTrigger(trigger);
        this.setPhoto(photo);
        this.setSocialSituation(socialSituation);
        this.setLocation(location);
    }

    public Mood() {
        // Empty constructor for Firebase - I touched this
        this.isDeserializing = true; // Set flag during deserialization - I touched this
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
     * Gets the trigger of the mood event.
     *
     * @return the trigger or null if not set
     */
    public String getTrigger() {
        return trigger;
    }

    /**
     * Sets the trigger of the mood event, ensuring it does not exceed 20 characters.
     *
     * @param trigger the trigger to set (max 20 characters)
     * @throws IllegalArgumentException if the trigger exceeds 20 characters
     */
    public void setTrigger(String trigger) {
        if (trigger != null && trigger.length() > 200) {
            throw new IllegalArgumentException("Trigger must be 200 characters at most.");
        }
        this.trigger = trigger;
        this.hasTrigger = trigger != null && !trigger.isEmpty();
    }

    public void setMoodID(String moodID) {
        this.moodID = moodID;
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
     * @return the photo as a byte array or null if not set
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Sets the photo associated with the mood event, ensuring it does not exceed 65536 bytes.
     *
     * @param photo the photo to set (max 65536 bytes)
     * @throws IllegalArgumentException if the photo exceeds 65536 bytes
     */
    public void setPhoto(byte[] photo) {
        if (photo != null && photo.length > 65536) {
            throw new IllegalArgumentException("Photo must be under 65536 bytes.");
        }
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
     * Checks if a trigger is associated with the mood event.
     *
     * @return true if a trigger is present, false otherwise
     */
    public boolean hasTrigger() {
        return hasTrigger;
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



    public String getTime() {
        return time;
    }


    private void updateTimestamp() {
        if (date == null || time == null) {
            throw new IllegalStateException("Date and time must not be null when updating timestamp");
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US);
            Log.d("Mood", "Parsing date and time: " + date + " " + time);
            Date parsedDate = sdf.parse(date + " " + time);
            if (parsedDate == null) {
                throw new ParseException("Parsed date is null", 0);
            }
            this.timestamp = new Timestamp(parsedDate);
            Log.d("Mood", "Timestamp set to: " + this.timestamp.toDate().toString());
        } catch (ParseException e) {
            Log.e("Mood", "Failed to parse date and time: " + date + " " + time, e);
            this.timestamp = Timestamp.now();
            Log.d("Mood", "Falling back to current timestamp: " + this.timestamp.toDate().toString());
        }
    }

    public void setDate(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date must not be null or empty");
        }
        this.date = date;
    }

    public void setTime(String time) {
        if (time == null || time.isEmpty()) {
            throw new IllegalArgumentException("Time must not be null or empty");
        }
        this.time = time;
        if (!isDeserializing) { // Only update timestamp if not deserializing - I touched this
            updateTimestamp();
        }
    }

    @PropertyName("timestamp")
    public Timestamp getTimestamp() { // Added getter for timestamp - I touched this
        return timestamp;
    }
//    public Timestamp getPostDate() {
//        return postDate;
//    }

//    public void setPostDate(Timestamp postDate) {
//        this.postDate = postDate;
//    }
    @PropertyName("timestamp")
    public void setTimestamp(Timestamp timestamp) { // Added setter for timestamp - I touched this
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "moodID=" + moodID +
                ", uid='" + uid + '\'' +
                ", postDate=" + postDate +
                ", trigger='" + (trigger == null ? "None" : trigger) + '\'' +
                ", socialSituation=" + (socialSituation == null ? "None" : socialSituation) +
                ", photo=" + (photo == null ? "None" : "Available") +
                ", location=" + (location == null ? "None" : location.toString()) +
                '}';
    }


}

