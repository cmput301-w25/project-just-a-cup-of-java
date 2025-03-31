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

public class AllFriendsFollowFragment extends Fragment {
    private FragmentFollowerAllFriendsBinding binding;
    private UserAdapter adapter;
    private List<User> userList;
    private FirebaseDB db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFollowerAllFriendsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
