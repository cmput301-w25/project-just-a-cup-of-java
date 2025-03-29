package com.example.justacupofjavapersonal.class_resources.mood;

import android.location.Location;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a Mood event with details such as emotional state, triggers,
 * social situations, location, and optional photo.
 */
public class Mood implements Serializable {

    private String moodID;
    private String privacy;
    private String uid;
    private String trigger;
    private String emotion;
    private String socialSituation;
    private String photo; // ✅ Base64 string

    private Date postDate;
    private Location location;

    private String date;
    private String time;

    private Timestamp timestamp;
    private EmotionalState state;

    private boolean hasPhoto = false;
    private boolean hasLocation = false;
    private boolean hasSocialSituation = false;
    private boolean hasTrigger = false;

    public transient boolean isDeserializing = false;

    public Mood() {
        this.isDeserializing = true;
    }

    public Mood(EmotionalState state, Date postDate) {
        if (state == null || postDate == null) {
            throw new IllegalArgumentException("State and postDate are required.");
        }
        this.state = state;
        this.postDate = postDate;
    }

    // --- Getters and setters ---

    public String getMoodID() {
        return moodID;
    }

    public void setMoodID(String moodID) {
        this.moodID = moodID;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        if (trigger != null && trigger.length() > 200) {
            throw new IllegalArgumentException("Trigger must be 200 characters or fewer.");
        }
        this.trigger = trigger;
        this.hasTrigger = trigger != null && !trigger.isEmpty();
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public EmotionalState getState() {
        return state;
    }

    public void setState(EmotionalState state) {
        this.state = state;
    }

    public String getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
        this.hasSocialSituation = socialSituation != null;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
        this.hasPhoto = photo != null && !photo.isEmpty();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.hasLocation = location != null;
    }

    public boolean hasPhoto() {
        return hasPhoto;
    }

    public boolean hasLocation() {
        return hasLocation;
    }

    public boolean hasSocialSituation() {
        return hasSocialSituation;
    }

    public boolean hasTrigger() {
        return hasTrigger;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date must not be null or empty");
        }
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        if (time == null || time.isEmpty()) {
            throw new IllegalArgumentException("Time must not be null or empty");
        }
        this.time = time;
        if (!isDeserializing) {
            updateTimestamp();
        }
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    @PropertyName("timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @PropertyName("timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    private void updateTimestamp() {
        if (date == null || time == null) {
            throw new IllegalStateException("Date and time must be set before timestamp update.");
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US);
            Date parsed = sdf.parse(date + " " + time);
            if (parsed != null) {
                this.timestamp = new Timestamp(parsed);
                Log.d("Mood", "Timestamp updated: " + this.timestamp.toDate());
            } else {
                throw new ParseException("Parsed date is null", 0);
            }
        } catch (ParseException e) {
            Log.e("Mood", "Failed to parse timestamp from date/time", e);
            this.timestamp = Timestamp.now(); // fallback
        }
    }

    @Override
    public String toString() {
        return "Mood{" +
                "moodID='" + moodID + '\'' +
                ", uid='" + uid + '\'' +
                ", emotion='" + emotion + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", socialSituation='" + socialSituation + '\'' +
                ", trigger='" + trigger + '\'' +
                ", hasPhoto=" + hasPhoto +
                '}';
    }
}
