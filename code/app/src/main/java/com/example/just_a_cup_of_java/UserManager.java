package com.example.just_a_cup_of_java;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/*
* A way to get the user information from the database
* */
public class UserManager {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void updateUser(String uid, Map<String, Object> updates, OnUserUpdatedListener listener) {
        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> listener.onUserUpdated())
                .addOnFailureListener(e -> listener.onUpdateFailed(e));
    }

    public void getUser(String uid, OnUserLoadedListener listener) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User userData = snapshot.toObject(User.class);
                        listener.onUserLoaded(userData);
                    } else {
                        listener.onUserNotFound();
                    }
                })
                .addOnFailureListener(e -> listener.onError(e));
    }

    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
        void onUserNotFound();
        void onError(Exception e);
    }

    public interface OnUserUpdatedListener {
        void onUserUpdated();
        void onUpdateFailed(Exception e);
    }
}
