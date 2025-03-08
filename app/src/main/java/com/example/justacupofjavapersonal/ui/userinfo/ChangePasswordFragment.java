package com.example.justacupofjavapersonal.ui.userinfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private EditText passwordBox, confirmPasswordBox;
    private Button changePasswordButton;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.change_password, container, false);

        mAuth = FirebaseAuth.getInstance();

        passwordBox = view.findViewById(R.id.passwordEditText);
        confirmPasswordBox = view.findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);

        // Set up button listener
        changePasswordButton.setOnClickListener(v -> changePassword());

        return view;
    }


    private void changePassword() {
        String newPassword = passwordBox.getText().toString().trim();
        String confirmPassword = confirmPasswordBox.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            passwordBox.setError("Password is required");
            return;
        }
        if (newPassword.length() < 6) {
            passwordBox.setError("Password must be at least 6 characters");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordBox.setError("Passwords do not match");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        user.updatePassword(newPassword)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}






