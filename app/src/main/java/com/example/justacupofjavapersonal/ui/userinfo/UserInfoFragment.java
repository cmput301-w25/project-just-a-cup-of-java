package com.example.justacupofjavapersonal.ui.userinfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserInfoFragment extends Fragment {
    private ImageView backArrow, profileImage;
    private TextView changePicture;
    private EditText editName, editUsername, editEmail, editBio;
    private Button btnChangePassword, btnSave, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userinfo, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backArrow = view.findViewById(R.id.back_arrow);
        profileImage = view.findViewById(R.id.profile_image);   //Still need to add this
        changePicture = view.findViewById(R.id.change_picture);
        editName = view.findViewById(R.id.edit_name);
        editUsername = view.findViewById(R.id.edit_username);
        editEmail = view.findViewById(R.id.edit_email);
        editBio = view.findViewById(R.id.edit_bio);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnSave = view.findViewById(R.id.btn_save);
        btnLogout = view.findViewById(R.id.btn_logout);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserData();
        setupListeners(view);
    }

    private void setupListeners(View view) {
        NavController navController = Navigation.findNavController(view);





        btnSave.setOnClickListener(v -> {
            saveProfileChanges();
            navController.navigate(R.id.navigation_home);
        });

        btnChangePassword.setOnClickListener(v -> {
            navController.navigate(R.id.navigation_change_password);
            Toast.makeText(getContext(), "Change password clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            navController.navigate(R.id.navigation_login);
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
        });

    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            editEmail.setText(currentUser.getEmail());
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                if (user.getName() != null) editName.setText(user.getName());
                                editUsername.setText(user.getUsername());
                                editEmail.setText(user.getEmail());
                                if (user.getBio() != null) editBio.setText(user.getBio());

                                if (documentSnapshot.contains("profilePic")) {
                                    Blob imageBlob = documentSnapshot.getBlob("profilePic");
                                    if (imageBlob != null) {
                                        byte[] imageBytes = imageBlob.toBytes();
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                        profileImage.setImageBitmap(bitmap);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveProfileChanges() {
        String name = editName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String bio = editBio.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Name is required");
            return;
        }
        if (username.isEmpty()) {
            editUsername.setError("Username is required");
            return;
        }
        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Invalid email format");
            return;
        }

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && user != null) {
                        boolean usernameTaken = false;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (!doc.getId().equals(user.getUid())) {
                                usernameTaken = true;
                                break;
                            }
                        }

                        if (usernameTaken) {
                            editUsername.setError("Username already taken");
                        } else {
                            user.setName(name);
                            user.setUsername(username);
                            user.setEmail(email);
                            user.setBio(bio);

                            db.collection("users").document(user.getUid())
                                    .set(user)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Failed to save profile", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }
}
