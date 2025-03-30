package com.example.justacupofjavapersonal.class_resources;

import com.example.justacupofjavapersonal.class_resources.mood.Mood;

import java.util.List;

public class UserMoodGroup {
    private User user;
    private List<Mood> moods;

    public UserMoodGroup(User user, List<Mood> moods) {
        this.user = user;
        this.moods = moods;
    }

    public User getUser() {
        return user;
    }

    public List<Mood> getMoods() {
        return moods;
    }
}
