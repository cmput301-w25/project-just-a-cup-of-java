package com.example.justacupofjavapersonal.class_resources.mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoodListBuilder {
    public static List<FeedItem> buildMoodList(List<Mood> moods) {
        List<FeedItem> finalList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        if (moods == null || moods.isEmpty()) return finalList;
        moods.sort((m1, m2) -> {
            if (m1.getPostDate() == null || m2.getPostDate() == null) return 0;
            return m1.getPostDate().compareTo(m2.getPostDate());
        });

        String lastDate = null;

        for (Mood mood : moods) {
            if (mood.getPostDate() == null) continue;

            String currentDate = dateFormat.format(mood.getPostDate());

            if (!currentDate.equals(lastDate)) {
                finalList.add(new FeedItem(currentDate));
                lastDate = currentDate;
            }

            finalList.add(new FeedItem(mood));
        }

        return finalList;
    }
}
