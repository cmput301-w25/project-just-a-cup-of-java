package com.example.justacupofjavapersonal.class_resources.mood;

/**
 * Represents an item in the feed which can either be a date header or a mood.
 */
public class FeedItem {
    private String dateHeader;
    private Mood mood;

    /**
     * Constructs a FeedItem with a date header.
     *
     * @param dateHeader the date header for this feed item
     */
    public FeedItem(String dateHeader) {
        this.dateHeader = dateHeader;
        this.mood = null;
    }

    /**
     * Constructs a FeedItem with a mood.
     *
     * @param mood the mood for this feed item
     */
    public FeedItem(Mood mood) {
        this.mood = mood;
        this.dateHeader = null;
    }

    /**
     * Checks if this feed item is a mood.
     *
     * @return true if this feed item is a mood, false otherwise
     */
    public boolean isMood() {
        return mood != null;
    }

    /**
     * Gets the date header of this feed item.
     *
     * @return the date header of this feed item, or null if this feed item is a mood
     */
    public String getDateHeader() {
        return dateHeader;
    }

    /**
     * Gets the mood of this feed item.
     *
     * @return the mood of this feed item, or null if this feed item is a date header
     */
    public Mood getMood() {
        return mood;
    }
}