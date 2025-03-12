package com.example.justacupofjavapersonal.class_resources.mood;

public enum EmotionalState {


    ANGER("ANGER 🤬", "#FF0000"),      // Red
    CONFUSION("CONFUSION 😵‍💫", "#FFA500"),  // Orange
    DISGUST("DISGUST 🤢", "#008000"),    // Green
    FEAR("FEAR 😨", "#800080"),       // Purple
    HAPPINESS("HAPPINESS 😊", "#FFFF00"),  // Yellow
    SADNESS("SADNESS 🥺", "#0000FF"),    // Blue
    SHAME("SHAME 😞", "#FF69B4"),      // Pink
    SURPRISE("SURPRISE 😲", "#00FFFF"),   // Cyan
    AWKWARD("AWKWARD 😅", "#2A2C57");   // Space Cadet/blue purple mix


    private final String state;
    private final String color;

    EmotionalState(String emoticon, String color) {
        this.state = emoticon;
        this.color = color;
    }

    /**
     * Returns the emoticon representing the current emotional state.
     *
     * @return a String containing the emoticon.
     */
    public String getState() {
        return state;
    }

    /**
     * Retrieves the color associated with the emotional state.
     *
     * @return the color as a String
     */
    public String getColor() {
        return color;
    }
}