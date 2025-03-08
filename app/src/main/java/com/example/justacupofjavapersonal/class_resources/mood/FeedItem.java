package com.example.justacupofjavapersonal.class_resources.mood;

public class FeedItem {
    private String dateHeader;
    private Mood mood;

    public FeedItem(String dateHeader) {
        this.dateHeader = dateHeader;
        this.mood = null;
    }
    
    public FeedItem(Mood mood) {
        this.mood = mood;
        this.dateHeader = null;
    }

    public boolean isMood() {
        return mood != null;
    }

    public String getDateHeader() {
        return dateHeader;
    }

    public Mood getMood() {
        return mood;
    }
}