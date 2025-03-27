package com.example.justacupofjavapersonal.ui.feed;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;
import com.example.justacupofjavapersonal.class_resources.mood.EmotionalState;
import com.example.justacupofjavapersonal.class_resources.mood.SocialSituation;
import com.example.justacupofjavapersonal.databinding.FragmentFeedBinding;
import com.example.justacupofjavapersonal.ui.map.MapFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FeedFragment is a fragment that displays a list of moods in a RecyclerView.
 * It initializes a list of Mood objects with sample data and sets up the RecyclerView
 * with a MoodDateAdapter to display the moods.
 */
public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private ArrayList<Mood> moods;
    private MoodDateAdapter moodDateAdapter;

    /**
     * Called to do initial creation of a fragment.
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moods = new ArrayList<>();

        Date t1 = new Date(System.currentTimeMillis() - 47363234L);
        Date t2 = new Date(System.currentTimeMillis() - 56152438L);
        Date t3 = new Date(System.currentTimeMillis() - 20252171L);
        Date t4 = new Date(System.currentTimeMillis() - 300525030L);

        // Create user objects
        User user1 = new User("person", "a", "a",  "a", "a", "a");
        User user2 = new User("person also", "a",  "a", "a", "a", "a");

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
        m3.setSocialSituation("WITH_TWO_TO_SEVERAL");
        m4.setPhoto("photo".getBytes());
        m4.setTrigger("trigger");
        m4.setLocation(l1);
        m4.setSocialSituation("WITH_ONE_OTHER");

        moods.add(m1);
        moods.add(m2);
        moods.add(m3);
        moods.add(m4);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
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

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     * This is where you should clean up resources related to the view.
     * It is important to call the superclass's implementation of this method.
     * In this implementation, the binding is set to null to avoid memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        binding.feedNearby.setOnClickListener(v ->
                navController.navigate(R.id.navigation_map)
        );

    }
}
