package com.example.justacupofjavapersonal.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.justacupofjavapersonal.R;


import com.example.justacupofjavapersonal.databinding.DialogLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * LoginDialogFragment is a dialog fragment that allows the user to login to the app.
 */
public class LoginDialogFragment extends DialogFragment {

    private DialogLoginBinding binding;
    private FirebaseAuth auth;
    private String email ="";
    private String password ="";


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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogLoginBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }


    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally tied to Activity.onResume of the containing Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set dialog width to 85% of screen width
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Apply background dim effect
            getDialog().getWindow().setDimAmount(0.9f);
        }
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initially disable login button
        binding.loginButton.setEnabled(false);


        // Add text change listener to enable login button when email & password are entered
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = binding.emailEditText.getText().toString().trim();
                password = binding.passwordEditText.getText().toString().trim();
                binding.loginButton.setEnabled(!email.isEmpty() && !password.isEmpty());
            }


            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.emailEditText.addTextChangedListener(textWatcher);
        binding.passwordEditText.addTextChangedListener(textWatcher);

        // Take the user back to the login page when cancel button is clicked
        binding.cancelButton.setOnClickListener(v -> {
            dismiss(); // First, close the dialog

            // Ensure navigation back to login page
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.navigation_login) {
                navController.navigate(R.id.navigation_login);
            }
        });

        // Handle login button click
        binding.loginButton.setOnClickListener(v -> {
            // TODO: Firebase authentication will be added here later
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Sign in", "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                dismiss(); // Close dialog for now
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign in", "signInWithEmail:failure", task.getException());
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                                dismiss(); // First, close the dialog

                                // Ensure navigation back to login page
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.navigation_login) {
                                    navController.navigate(R.id.navigation_login);
                                }
                            }
                        }
                    });
        });
    }

    /**
     * This method is called when the view previously created by onCreateView has been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
