package com.example.justacupofjavapersonal.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;
import com.example.justacupofjavapersonal.class_resources.mood.EmotionalState;

import com.example.justacupofjavapersonal.databinding.FragmentFeedBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private ArrayList<Mood> moods;
    private MoodDateAdapter moodDateAdapter;
    private MoodListBuilder moodListBuilder;
    private FeedItem feedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moods = new ArrayList<>();
        // Integer moodID, User user, EmotionalState state, Date postDate
        // String name, String email, String password, String bio, String profilePic, String username, String uid
        moods.add(new Mood(1, new User("person", "a", "a", "a", "a", "a", "a"), EmotionalState.HAPPINESS, new Date()));
        moods.add(new Mood(1, new User("person also", "a", "a", "a", "a", "a", "a"), EmotionalState.HAPPINESS, new Date()));
        moods.add(new Mood(1, new User("????", "a", "a", "a", "a", "a", "a"), EmotionalState.HAPPINESS, new Date()));
        moods.add(new Mood(1, new User("person", "a", "a", "a", "a", "a", "a"), EmotionalState.HAPPINESS, new Date()));
        

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        moodListBuilder = new MoodListBuilder();
        List<FeedItem> finalList = moodListBuilder.buildMoodList(moods);
        moodDateAdapter = new MoodDateAdapter(finalList);
        binding.recyclerView.setAdapter(moodDateAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}