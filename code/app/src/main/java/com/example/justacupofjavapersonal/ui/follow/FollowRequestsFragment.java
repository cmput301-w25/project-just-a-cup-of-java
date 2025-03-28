package com.example.justacupofjavapersonal.ui.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.justacupofjavapersonal.databinding.FragmentFollowRequestsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowRequestsFragment extends Fragment {
    private FragmentFollowRequestsBinding binding;
    private UserRequestAdapter adapter;
    private List<User> userList;

    private FirebaseDB db;
    private FirebaseUser currUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFollowRequestsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new FirebaseDB();
        currUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userList = new ArrayList<>();
        if (currUser != null) {
            loadRequests(currUser.getUid());
        } else { //Placeholder requests
            userList.add(new User("Panda Express"));
            userList.add(new User("PF Changs"));
        }

        adapter = new UserRequestAdapter(userList, new UserRequestAdapter.OnItemClickListener() {
            @Override
            public void onAcceptClick(int position) {
                Toast.makeText(requireContext(), "Accepted Follow Request", Toast.LENGTH_LONG).show();
                adapter.removeItem(position);
            }

            @Override
            public void onDenyClick(int position) {
                Toast.makeText(requireContext(), "Denied Follow Request", Toast.LENGTH_LONG).show();
                adapter.removeItem(position);
            }
        });

        binding.recyclerView.setAdapter(adapter);
        // Get NavController for navigating between fragments
        NavController navController = Navigation.findNavController(view);

        binding.friendsButtonRequestsFragment.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_follower_requests_to_notifications);
        });
    }

    private void loadRequests(String userID) {
        db.getAllRequests(userID, users -> {
            userList.clear();
            userList.addAll(users);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}