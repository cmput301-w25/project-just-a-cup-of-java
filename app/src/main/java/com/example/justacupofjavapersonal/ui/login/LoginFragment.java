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
import com.example.justacupofjavapersonal.databinding.FragmentLoginBinding;
import com.example.justacupofjavapersonal.class_resources.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String email ="";
    private String password ="";
    private String confirmPassword = "";
    private FragmentLoginBinding binding;

    private void saveUserToFirestore(String uid, String email) {
        User user = new User(uid, email);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireActivity(), "Account Created Sucessfully", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Failed to Create Account", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        binding.loginButton.setOnClickListener(v -> {
            LoginDialogFragment loginDialog = new LoginDialogFragment();
            loginDialog.show(getParentFragmentManager(), "login_dialog");
            navController.navigate(R.id.navigation_home);
        });

        binding.signupButton.setEnabled(false);

        // Add text change listener to enable signup button when email & password are entered
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
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    saveUserToFirestore(user.getUid(), email);
                                    Log.d("Auth success", "User created");
                                } else {
                                    Toast.makeText(requireActivity(), "Authentication Failed, User not Created", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}
