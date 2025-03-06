package com.example.just_a_cup_of_java;

import static androidx.fragment.app.FragmentManager.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText editEmail, editUsername, editPassword;
    private Button signup;

    //Check username availability before creating account
    private void checkUsernameAvailability(String username, String password, String email) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        createUserWithEmailPassword(username, email, password);
                    } else {
                        //Do something to say that the username is taken
                        Toast.makeText(this, "Choose another username, this one is already taken", Toast.LENGTH_LONG);
                    }
                });
    }

    private void createUserWithEmailPassword(String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            saveUserToFirestore(user.getUid(), username, email);
                        } else {
                            //Toast.makeText(MainActivity.this,"Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String uid, String username, String email) {
        User user = new User(uid, username, email);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    //Toast.makeText(this, "Account Created Sucessfully", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editEmail = findViewById(R.id.edit_signup_email);
        editUsername = findViewById(R.id.edit_signup_username);
        editPassword = findViewById(R.id.edit_signup_password);
        signup = findViewById(R.id.signup_button);

        //Create event binder to create new account
        signup.setOnClickListener(view -> {
            String email = editEmail.getText().toString();
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();

            if (!email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                Log.e("SetOnClickListener", "Empty checker ran");
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    saveUserToFirestore(user.getUid(), username, email);
                                } else {
                                    //Toast.makeText(MainActivity.this,"Authentication Failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        User user = new User("test", "test", "test");
        db.collection("user").document("test").set(user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }


}