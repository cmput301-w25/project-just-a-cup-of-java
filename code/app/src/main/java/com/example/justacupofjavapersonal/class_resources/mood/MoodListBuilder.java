package com.example.justacupofjavapersonal.class_resources.mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utility class that provides a method to build a list of FeedItem objects
 * from a list of Mood objects. The resulting list is sorted by post date in descending order and
 * includes date headers for each unique date.
 */
public class MoodListBuilder {
    /**
     * Builds a list of FeedItem objects from a list of Mood objects.
     * The resulting list will be sorted by post date in descending order.
     * Each unique date will be represented as a separate FeedItem in the list.
     *
     * @param moods the list of Mood objects to be converted into FeedItem objects
     * @return a list of FeedItem objects, sorted by post date and grouped by unique dates
     */
    public static List<FeedItem> buildMoodList(List<Mood> moods) {
        List<FeedItem> finalList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        if (moods == null || moods.isEmpty()) return finalList;
        moods.sort((m1, m2) -> {
            if (m1.getPostDate() == null || m2.getPostDate() == null) return 0;
            return m2.getPostDate().compareTo(m1.getPostDate());
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
