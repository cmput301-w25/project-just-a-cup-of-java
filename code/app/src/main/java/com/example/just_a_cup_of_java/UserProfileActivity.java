package com.example.just_a_cup_of_java;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImage;
    private EditText etName, etUsername, etEmail;
    private Button btnSave, btnLogout, btnChangePassword;
    private TextView changePicture;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login if not signed in
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        userRef = db.collection("Users").document(currentUser.getUid());
        storageRef = storage.getReference().child("profile_pictures/" + currentUser.getUid());

        // Initialize UI elements
        profileImage = findViewById(R.id.profileImage);
        changePicture = findViewById(R.id.changePicture);
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Load existing user data
        loadUserProfile();

        // Change Picture
        changePicture.setOnClickListener(v -> selectImageFromGallery());

        // Save Button
        btnSave.setOnClickListener(v -> saveUserProfile());

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
        });

        // Change Password
        btnChangePassword.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, ChangePasswordActivity.class)));
    }

    /**
     * Load user profile data from Firestore
     */
    private void loadUserProfile() {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                etName.setText(documentSnapshot.getString("name"));
                etUsername.setText(documentSnapshot.getString("username"));
                etEmail.setText(documentSnapshot.getString("email"));
                String profilePicUrl = documentSnapshot.getString("profilePic");

                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                    // Load image using a library like Glide or Picasso
                    Glide.with(this).load(profilePicUrl).into(profileImage);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Opens gallery to select an image
     */
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles result from gallery selection and uploads to Firebase
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
                uploadImageToFirebase(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Uploads selected image to Firebase Storage
     */
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userRef.update("profilePic", imageUrl);
                        Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            );
        }
    }

    /**
     * Saves updated user profile information to Firestore & FirebaseAuth
     */
    private void saveUserProfile() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("username", username);
        userMap.put("email", email);

        // Update FirebaseAuth email
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updateEmail(email).addOnSuccessListener(aVoid -> {
                userRef.set(userMap)
                        .addOnSuccessListener(aVoid1 -> Toast.makeText(UserProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show());
        }
    }
}
