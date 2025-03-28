//IQRAS WORKING CODE
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
        //firebaseDB = new FirebaseDB();
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

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        NavController navController = Navigation.findNavController(view);
        binding.followerRequestsFriends.setOnClickListener(v ->
                navController.navigate(R.id.action_navigation_notification_to_follower_requests)
        );

        //loadUsers();
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

//        // Handle SearchView input
//        binding.userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchUsers(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchUsers(newText);
//                return true;
//            }
//        });
//
//        // Navigate to requests fragment when "Requests" button is clicked
//        NavController navController = Navigation.findNavController(view);
//        binding.followerRequestsFriends.setOnClickListener(v -> {
//            navController.navigate(R.id.action_navigation_notification_to_follower_requests);
//        });
//    }
//
//    private void searchUsers(String query) {
//        if (query.trim().isEmpty()) {
//            userList.clear();
//            adapter.notifyDataSetChanged();
//            return;
//        }
//
//        firebaseDB.searchUsers(query, new FirebaseDB.OnUserSearchListener() {
//            @Override
//            public void onResults(List<User> users) {
//                userList.clear();
//                userList.addAll(users);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Toast.makeText(requireContext(), "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }




//GARRICKS FOLLOWING CODE
//package com.example.justacupofjavapersonal.ui.follow;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.justacupofjavapersonal.R;
//import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
//import com.example.justacupofjavapersonal.class_resources.User;
//import com.example.justacupofjavapersonal.databinding.FragmentFollowerAllFriendsBinding;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AllFriendsFollowFragment extends Fragment {
//    private FragmentFollowerAllFriendsBinding binding;
//    private UserAdapter adapter;
//    private List<User> userList;
//    private FirebaseDB db;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentFollowerAllFriendsBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//        return root;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        db = new FirebaseDB();
//
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        userList = new ArrayList<>();
//        userList.add(new User("Panda Express"));
//        userList.add(new User("PF Changs"));
//
//        adapter = new UserAdapter(userList, position -> {
//            Toast.makeText(requireContext(), "Follow Request Sent" + userList.get(position).getName(), Toast.LENGTH_LONG).show();
//        }, db);
//
//        binding.recyclerView.setAdapter(adapter);
//        // Get NavController for navigating between fragments
//        NavController navController = Navigation.findNavController(view);
//
//        // Add click listener to go to requests page
//        binding.followerRequestsFriends.setOnClickListener(v -> {
//            navController.navigate(R.id.action_navigation_notification_to_follower_requests);
//        });
//
//        loadUsers();
//
//        binding.searchButton.setOnClickListener(v -> {
//            String search = binding.userSearch.getText().toString().trim();
//            searchUsers(search);
//        });
//    }
//
//    private void loadUsers() {
//        db.getAllUsers(users -> {
//            userList.clear();
//            userList.addAll(users);
//            adapter.notifyDataSetChanged();
//        });
//    }
//
//    private void searchUsers(String search) {
//        db.searchUsers(search, users -> {
//            userList.clear();
//            userList.addAll(users);
//            adapter.notifyDataSetChanged();
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}