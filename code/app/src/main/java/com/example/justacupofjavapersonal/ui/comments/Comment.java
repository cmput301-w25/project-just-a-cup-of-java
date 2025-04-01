package com.example.justacupofjavapersonal.ui.comments;

import com.google.firebase.Timestamp;

//java docs code

/**
 * Represents a comment in a mood.
 *
 */
public class Comment {
    private String userId;
    private String userName;
    private String text;
    private Timestamp timestamp;

    public Comment() {
        // Required for Firestore deserialization
    }

    public Comment(String userId, String userName, String text, Timestamp timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
