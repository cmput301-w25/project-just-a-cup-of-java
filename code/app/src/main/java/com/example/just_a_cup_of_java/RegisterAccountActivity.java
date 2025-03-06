package com.example.just_a_cup_of_java;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterAccountActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    private void registerUser() {
        String username;
        String password;
        String email = username + "@moodspace.com";
        String userId;
    }

    private void checkUsernameAvailability(String username, String password, String email) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        createUserWithEmailPassword(username, email, password);
                    } else {
                        //Do something to say that the username is taken
                    }
                });
    }

    private void createUserWithEmailPassword(String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToFirestore(firebaseUser.getUid(), username, email);
                        }
                    }
                });
    }

    private void saveUserToFirestore(String uid, String username, String email) {
        User user = new User(uid, username, email);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Account Created Sucessfully", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                });
    }
}
