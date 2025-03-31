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
import com.example.justacupofjavapersonal.ui.follow.UserRequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying and managing follow requests.
 *
 * <p>This fragment retrieves follow requests from Firebase and allows the user to accept or deny them.</p>
 */
public class FollowRequestsFragment extends Fragment {
    private FragmentFollowRequestsBinding binding;
    private UserRequestAdapter adapter;
    private List<User> userList;
    private FirebaseDB db;
    private FirebaseUser currUser;

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     * @return The root view for the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFollowRequestsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
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
        currUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.requestsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

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
        }, db);

        binding.requestsRecyclerView.setAdapter(adapter);
        // Get NavController for navigating between fragments
        NavController navController = Navigation.findNavController(view);

        binding.friendsButtonRequestsFragment.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_follower_requests_to_notifications);
        });
    }

    /**
     * Loads follow requests from Firebase and updates the RecyclerView.
     *
     * @param userID The current user's ID.
     */
    private void loadRequests(String userID) {
        db.getAllRequests(userID, new FirebaseDB.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> users) {
                userList.clear();
                userList.addAll(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUsersRetrievedFailed(Exception e) {
                Toast.makeText(requireContext(), "Failed to load requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
