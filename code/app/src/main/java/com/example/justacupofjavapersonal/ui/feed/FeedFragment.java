package com.example.justacupofjavapersonal.ui.feed;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;
import com.example.justacupofjavapersonal.class_resources.mood.EmotionalState;
import com.example.justacupofjavapersonal.class_resources.mood.SocialSituation;
import com.example.justacupofjavapersonal.databinding.FragmentFeedBinding;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private ArrayList<Mood> moods;
    private MoodDateAdapter moodDateAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moods = new ArrayList<>();

        Date t1 = new Date(System.currentTimeMillis() - 47363234L);
        Date t2 = new Date(System.currentTimeMillis() - 56152438L);
        Date t3 = new Date(System.currentTimeMillis() - 20252171L);
        Date t4 = new Date(System.currentTimeMillis() - 300525030L);

        // Create user objects
        User user1 = new User("person", "a", "a", "a", "a", "a", "a");
        User user2 = new User("person also", "a", "a", "a", "a", "a", "a");

        // Pass user IDs instead of full User objects
        Mood m1 = new Mood(EmotionalState.HAPPINESS, t1);
        Mood m2 = new Mood(EmotionalState.HAPPINESS, t2);
        Mood m3 = new Mood(EmotionalState.HAPPINESS, t3);
        Mood m4 = new Mood(EmotionalState.HAPPINESS, t4);

        Location l1 = new Location("location");
        l1.setLatitude(1.0);
        l1.setLongitude(1.0);

        m1.setPhoto("photo".getBytes());
        m2.setTrigger("trigger");
        m2.setLocation(l1);
        m3.setSocialSituation(SocialSituation.WITH_TWO_TO_SEVERAL);
        m4.setPhoto("photo".getBytes());
        m4.setTrigger("trigger");
        m4.setLocation(l1);
        m4.setSocialSituation(SocialSituation.WITH_ONE_OTHER);

        moods.add(m1);
        moods.add(m2);
        moods.add(m3);
        moods.add(m4);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<FeedItem> finalList = MoodListBuilder.buildMoodList(moods);

        moodDateAdapter = new MoodDateAdapter(finalList);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(moodDateAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
