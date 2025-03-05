package com.example.just_a_cup_of_java;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText username, bio, name, email;
    private UserManager userManager;
    private User currentUser;
    private String uid;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(/**/);
        username = findViewById(R.id.editUsername);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void loadUserData() {
        userManager.getUser(currentUser.getUid(), new UserManager.OnUserLoadedListener() {
            @Override
            public void onUserLoaded(User user) {
                currentUser = user;
            }

            @Override
            public void onUserNotFound() {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void saveProfileUpdates() {
        String newUsername = editUsername.getText().toString().trim();
        String newBio = editBio.getText().toString().trim();
        String newName = editName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();



        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);
        updates.put("bio", newBio);




    }
}
