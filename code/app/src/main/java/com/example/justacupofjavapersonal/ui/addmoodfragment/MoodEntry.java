package com.example.justacupofjavapersonal.ui.addmoodfragment;

public class MoodEntry {
    private String moodText;
    private String photoBase64;
    private String photoUri;

    public MoodEntry(String moodText, String photoBase64, String photoUri) {
        this.moodText = moodText;
        this.photoBase64 = photoBase64;
        this.photoUri = photoUri;
    }

    public String getMoodText() {
        return moodText;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}
