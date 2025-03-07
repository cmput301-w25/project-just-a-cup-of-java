package com.example.justacupofjavapersonal.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        // Navigate to home on button click
        binding.loginButton.setOnClickListener(v -> {
            LoginDialogFragment loginDialog = new LoginDialogFragment();
            loginDialog.show(getParentFragmentManager(), "login_dialog");
            navController.navigate(R.id.navigation_home);
        });
    }
}
