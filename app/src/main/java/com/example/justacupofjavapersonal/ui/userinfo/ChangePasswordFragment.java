package com.example.justacupofjavapersonal.ui.userinfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

/**
 * A {@link Fragment} that provides a user interface to change the current user's password.
 * <p>
 * This fragment allows a user to input a new password and confirm it. It validates the inputs
 * and, upon successful validation, updates the user's password using Firebase Authentication.
 * </p>
 */
public class ChangePasswordFragment extends Fragment {

    private EditText passwordBox, confirmPasswordBox;
    private Button changePasswordButton;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("ChangePasswordFragment", "onCreateView started");
        View view = inflater.inflate(R.layout.change_password, container, false);

        // Initialize Firebase Authentication instance.
        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements.
        passwordBox = view.findViewById(R.id.passwordEditText);
        confirmPasswordBox = view.findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);

        // Set up the change password button click listener.
        changePasswordButton.setOnClickListener(v -> {
            Log.e("ChangePasswordFragment", "Change password button clicked");
            changePassword();
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        view.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            Log.e("ChangePasswordFragment", "Back arrow clicked");
            navController.navigate(R.id.navigation_user_info);
        });
    }

    /**
     * Attempts to change the user's password using the provided new password and confirmation password.
     * <p>
     * The method performs several validation checks:
     * <ul>
     *     <li>Ensures that the new password field is not empty.</li>
     *     <li>Checks that the new password is at least 6 characters long.</li>
     *     <li>Confirms that the new password and the confirmation password match.</li>
     * </ul>
     * If all validations pass, the method updates the user's password through Firebase Authentication,
     * and displays a corresponding Toast message for success or failure.
     * </p>
     */
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
