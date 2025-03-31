package com.example.justacupofjavapersonal.ui.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.databinding.FragmentFollowerAllFriendsBinding;
import com.example.justacupofjavapersonal.ui.follow.UserAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying all users that can be followed.
 *
 * <p>This fragment retrieves a list of users from Firebase and allows searching for specific users.
 * Users can send follow requests from this screen.</p>
 */
public class AllFriendsFollowFragment extends Fragment {
    private FragmentFollowerAllFriendsBinding binding;
    private UserAdapter adapter;
    private List<User> userList;
    private FirebaseDB db;

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     * @return The root view for the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFollowerAllFriendsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    /**
     * Called immediately after the fragment's view has been created.
     *
     * @param view               The fragment's root view.
     * @param savedInstanceState If non-null, the fragment is being re-created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new FirebaseDB();
        userList = new ArrayList<>();

        // Initialize adapter
        adapter = new UserAdapter(userList, position -> {
            Toast.makeText(requireContext(), "Follow Request Sent: " + userList.get(position).getName(), Toast.LENGTH_LONG).show();
        },db);

        binding.allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.allUsersRecyclerView.setAdapter(adapter);

        NavController navController = Navigation.findNavController(view);
        binding.followerRequestsFriends.setOnClickListener(v ->
                navController.navigate(R.id.action_navigation_notification_to_follower_requests)
        );

        binding.searchButton.setOnClickListener(v -> {
            String search = binding.userSearch.getText().toString().trim();
            searchUsers(search);
        });
        loadUsers();
    }

    /**
     * Loads all users from Firebase and updates the RecyclerView.
     */
    private void loadUsers() {
        db.getAllUsers(new FirebaseDB.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> users) {
                userList.clear();
                userList.addAll(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUsersRetrievedFailed(Exception e) {
                Toast.makeText(requireContext(), "Failed to load users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Searches for users based on the given query and updates the RecyclerView.
     *
     * @param search The search query string.
     */
    private void searchUsers(String search) {
        db.searchUsers(search, new FirebaseDB.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> users) {
                userList.clear();
                userList.addAll(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUsersRetrievedFailed(Exception e) {
                Toast.makeText(requireContext(), "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when the view is destroyed to clean up binding references.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
