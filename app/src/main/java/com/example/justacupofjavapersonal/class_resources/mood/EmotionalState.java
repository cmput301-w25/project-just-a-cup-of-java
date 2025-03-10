package com.example.justacupofjavapersonal.class_resources.mood;

public enum EmotionalState {


    ANGER("🤬", "#FF0000"),      // Red
    CONFUSION("😵‍💫", "#FFA500"),  // Orange
    DISGUST("🤢", "#008000"),    // Green
    FEAR("😨", "#800080"),       // Purple
    HAPPINESS("😊", "#FFFF00"),  // Yellow
    SADNESS("🥺", "#0000FF"),    // Blue
    SHAME("😞", "#FF69B4"),      // Pink
    SURPRISE("😲", "#00FFFF"),   // Cyan
    AWKWARD("😅", "#2A2C57");   // Space Cadet/blue purple mix


    private final String emoticon;
    private final String color;

    EmotionalState(String emoticon, String color) {
        this.emoticon = emoticon;
        this.color = color;
    }

    public String getEmoticon() {
        return emoticon;
    }

    public String getColor() {
        return color;
    }
}