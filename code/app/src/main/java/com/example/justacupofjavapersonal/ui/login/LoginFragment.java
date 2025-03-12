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

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String email = "";
    private String password = "";
    private String confirmPassword = "";
    private FragmentLoginBinding binding;

    public interface FirebaseOperationListener {
        void onFirebaseOperationComplete(boolean success);
    }

    private FirebaseOperationListener firebaseOperationListener;

    public void setFirebaseOperationListener(FirebaseOperationListener listener) {
        this.firebaseOperationListener = listener;
        Log.d(TAG, "FirebaseOperationListener set");
    }

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "onCreateView called");
        return binding.getRoot();
    }

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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = binding.emailEditText.getText().toString().trim();
                password = binding.passwordEditText.getText().toString().trim();
                confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

                binding.signupButton.setEnabled(!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty());
            }

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