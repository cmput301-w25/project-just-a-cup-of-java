package com.example.justacupofjavapersonal.class_resources.mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
    public static List<FeedItem> buildFeedList(List<Mood> moods) {
        List<FeedItem> feedItems = new ArrayList<>();
        Map<String, List<Mood>> dateMap = new TreeMap<>(Collections.reverseOrder()); // to show latest date first

        for (Mood mood : moods) {
            if (mood.getDate() == null) continue;
            String date = mood.getDate();
            if (!dateMap.containsKey(date)) {
                dateMap.put(date, new ArrayList<>());
            }
            dateMap.get(date).add(mood);
        }

        for (String date : dateMap.keySet()) {
            feedItems.add(new FeedItem(date)); // date header
            for (Mood mood : dateMap.get(date)) {
                feedItems.add(new FeedItem(mood)); // actual mood
            }
        }

        return feedItems;
    }

    public static List<FeedItem> buildMoodList(List<Mood> moods) {
        List<FeedItem> finalList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        if (moods == null || moods.isEmpty()) return finalList;

        // Optional: sort using getDate() fallback (string field)
        moods.sort((m1, m2) -> {
            if (m1.getPostDate() == null || m2.getPostDate() == null) return 0;
            return m2.getPostDate().compareTo(m1.getPostDate());
        });

        String lastDate = null;

        for (Mood mood : moods) {
            String currentDate;

            // Try using postDate if available
            if (mood.getPostDate() != null) {
                currentDate = dateFormat.format(mood.getPostDate());
            } else if (mood.getDate() != null) {
                // Fallback to string `date` field (e.g., "23-03-2025")
                currentDate = mood.getDate();
            } else {
                currentDate = "Unknown Date";
            }

            // Only add a new header if it's different from the last
            if (!currentDate.equals(lastDate)) {
                finalList.add(new FeedItem(currentDate));
                lastDate = currentDate;
            }

            finalList.add(new FeedItem(mood)); // Still add the mood regardless of postDate
        }

        return finalList;
    }

}
