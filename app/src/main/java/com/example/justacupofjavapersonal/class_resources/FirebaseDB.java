package com.example.justacupofjavapersonal.class_resources;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDB {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Updates the user information in firebase
     * @param uid
     * @param updates
     * @param listener
     */
    public void updateUser(String uid, Map<String, Object> updates, OnUpdatedListener listener) {
        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> listener.onUpdateSuccess())
                .addOnFailureListener(e -> listener.onUpdateFailed(e));

        if (updates.containsKey("email")) {
            FirebaseUser user = auth.getCurrentUser();
            String email = updates.get("email").toString();
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                     public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           Log.d("Update User Profile", "Email updated");
                            }
                           }
                     });
        } else if (updates.containsKey("password")) {
            FirebaseUser user = auth.getCurrentUser();
            String password = updates.get("password").toString();
            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Update User Details", "Password Updated");
                            }
                        }
                    });
        }
    }

    //Add method for updating email and password, separate from updates

    // Add implementation later
//    public User getUserData(String uid) {
//        DocumentReference docRef = db.collection("users").document(uid);
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            User userData;
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                userData = documentSnapshot.toObject(User.class);
//
//            }
//            return userData;
//        });
//    }

    public interface OnUpdatedListener {
        void onUpdateSuccess();
        void onUpdateFailed(Exception e);
    }

    /**
     * Adds the selected mood to firebase
     * @param mood
     * @param uid
     */
    public void addMoodtoDB(Mood mood, String uid) {
        String moodID = db.collection("moods").document().getId();

        db.collection("moods").document(moodID)
                .set(mood)
                .addOnSuccessListener(aVoid -> Log.d("Add Mood", "Mood stored in DB"))
                .addOnFailureListener(e -> Log.e("Add Mood", "Mood not saved to DB", e));

        // Add user id to a mood to connect it back to the user
        db.collection("moods").document(moodID)
                .update("uid", uid);
    }

    /**
     * Updates a given mood
     * @param moodID
     * @param updates
     * @param listener
     */
    public void updateMood(String moodID, Map<String, Object> updates, OnUpdatedListener listener) {
        db.collection("moods").document(moodID)
                .update(updates)
                .addOnSuccessListener(aVoid -> listener.onUpdateSuccess())
                .addOnFailureListener(e -> listener.onUpdateFailed(e));
    }

    /**
     * Deletes the selected mood from firebase
     * @param mood
     */
    public void deleteMood(Mood mood) {
        db.collection("moods").document(mood.getMoodId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Delete Mood", "Mood Successfully deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Delete Mood", "Error Deleteing mood", e);
                    }
                });
    }

    /**
     * Get the list of user moods
     * @param uid
     * @return
     */
    // Not sure if this works
    public List<Mood> getMoods(String uid) {
        CollectionReference moodRef = db.collection("moods");
        List<Mood> userMoods = new ArrayList<>();
        moodRef.addSnapshotListener((value, error) -> {
            if (error != null){
                Log.e("Firestore", error.toString());
            }
            if (value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot snapshot : value) {
                    //String moodID, String uid, Date postDate, String trigger, byte[] photo, EmotionalState state, SocialSituation socialSituation, Location location
                    String moodID = snapshot.getString("moodID");
                    String userID = snapshot.getString("uid");
                    Date postDate = snapshot.getDate("postDate");
                    String trigger = snapshot.getString("trigger");
                    byte[] photo = (byte[]) snapshot.get("photo");
                    EmotionalState state = (EmotionalState) snapshot.get("state");
                    SocialSituation socialSituation = (SocialSituation) snapshot.get("socialSituation");
                    Location location = (Location) snapshot.get("location");

                    if (userID.equals(uid)) {
                        userMoods.add(new Mood(moodID, userID, postDate, trigger, photo, state, socialSituation, location));
                    }

                }
            }
        });
        return userMoods;
    }

    public interface OnMoodLoadedListener {
        void onMoodsLoaded(List<Mood> moods);
        void onMoodsLoadedFailed(Exception e);
    }

    public void addFollower(User follower, User followee) {
        db.collection("follows").document();
    }

    public void removeFollwer(User follower, User followee) {

    }

    public void getFollowers(User follower, User followee) {

    }
}
