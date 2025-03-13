package com.example.justacupofjavapersonal.ui.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.databinding.FragmentFollowerAllFriendsBinding;
import com.example.justacupofjavapersonal.ui.notifications.NotificationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class AllFriendsFollowFragment extends Fragment {
    private FragmentFollowerAllFriendsBinding binding;
    private UserAdapter adapter;
    private List<User> userList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFollowerAllFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userList = new ArrayList<>();
        userList.add(new User("Panda Express"));
        userList.add(new User("PF Changs"));

        adapter = new UserAdapter(userList, position -> {
            Toast.makeText(requireContext(), "Follow Request Sent" + userList.get(position).getName(), Toast.LENGTH_LONG).show();
        });

        binding.recyclerView.setAdapter(adapter);
        // Get NavController for navigating between fragments
        NavController navController = Navigation.findNavController(view);

        // Add click listener for follow button

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
