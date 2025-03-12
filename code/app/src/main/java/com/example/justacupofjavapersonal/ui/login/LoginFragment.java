package com.example.justacupofjavapersonal.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.databinding.FragmentLoginBinding;
import com.example.justacupofjavapersonal.class_resources.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * LoginFragment is a fragment that allows the user to login or sign up to the app.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String email = "";
    private String password = "";
    private String confirmPassword = "";
    private FragmentLoginBinding binding;

    /**
     * Interface for listeners to be notified when a Firebase operation is complete.
     */
    public interface FirebaseOperationListener {
        void onFirebaseOperationComplete(boolean success);
    }

    private FirebaseOperationListener firebaseOperationListener;

    /**
     * Called to do initial creation of a fragment.
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    public void setFirebaseOperationListener(FirebaseOperationListener listener) {
        this.firebaseOperationListener = listener;
        Log.d(TAG, "FirebaseOperationListener set");
    }

    /**
     * Saves the user to Firestore.
     *
     * @param uid   The user's unique ID.
     * @param email The user's email.
     */
    private void saveUserToFirestore(String uid, String email) {
        User user = new User(uid, email);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireActivity(), "Account Created Successfully", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Failed to Create Account", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Firestore save failed: " + e.getMessage());
                });
    }

    /**
     * Called to do initial creation of a fragment.
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "onCreateView called");
        return binding.getRoot();
    }

    /**
     * This method is called when the view previously created by onCreateView has been detached from the fragment.
     * The next time the fragment needs to be displayed, a new view will be created.
     *
     * @param view               The view that was created in onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        NavController navController = Navigation.findNavController(view);

        binding.loginButton.setOnClickListener(v -> {
            LoginDialogFragment loginDialog = new LoginDialogFragment();
            loginDialog.show(getParentFragmentManager(), "login_dialog");
            navController.navigate(R.id.navigation_home);
        });

        binding.signupButton.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            /**
             * This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
             *
             * @param s      The text the TextView is displaying.
             * @param start  The starting index of the text that is about to be replaced.
             * @param count  The length of the text that is about to be replaced.
             * @param after  The length of the new text that will replace the old text.
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
             *
             * @param s      The text the TextView is displaying.
             * @param start  The starting index of the text that was replaced.
             * @param before The length of the text that was replaced.
             * @param count  The length of the new text.
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = binding.emailEditText.getText().toString().trim();
                password = binding.passwordEditText.getText().toString().trim();
                confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

                binding.signupButton.setEnabled(!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty());
            }

            /**
             * This method is called to notify you that, somewhere within s, the text has been changed.
             *
             * @param s The text the TextView is displaying.
             */
            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.emailEditText.addTextChangedListener(textWatcher);
        binding.passwordEditText.addTextChangedListener(textWatcher);
        binding.confirmPasswordEditText.addTextChangedListener(textWatcher);

        binding.signupButton.setOnClickListener(v -> {
            if (!password.equals(confirmPassword)) {
                Toast.makeText(requireActivity(), "Passwords don't match, please try again", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Starting Firebase signup for email: " + email);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "Firebase operation completed, success: " + task.isSuccessful());
                                if (firebaseOperationListener != null) {
                                    firebaseOperationListener.onFirebaseOperationComplete(task.isSuccessful());
                                    Log.d(TAG, "Listener notified");
                                }

                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    saveUserToFirestore(user.getUid(), email);
                                    Log.d(TAG, "User created, navigating to UserInfo");
                                    navController.navigate(R.id.navigation_user_info);
                                } else {
                                    Toast.makeText(requireActivity(), "Authentication Failed, User not Created", Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "Authentication failed: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }
}