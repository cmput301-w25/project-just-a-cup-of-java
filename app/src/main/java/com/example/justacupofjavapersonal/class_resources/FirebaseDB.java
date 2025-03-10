package com.example.justacupofjavapersonal.class_resources;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.justacupofjavapersonal.class_resources.mood.EmotionalState;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.SocialSituation;
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
import com.example.justacupofjavapersonal.class_resources.User;

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
     * Takes in a userId, a map of updates and a listener to update user information in firebase.
     * Additonally updates the user's email and password in firebase authentication if those were
     * updated as well.
     * @param uid UserID of the specified user
     * @param updates Map of fields that are to be updated
     * @param listener Listener to keep track of updates
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

    /**
     * Takes in a userID and fetches the user from Firebase, and returns as a user object.
     * @param uid UserID of the specified user
     * @return the user with the specified uid
     */
    public User getUser(String uid) {
        DocumentReference docRef = db.collection("users").document(uid);
        final User[] userData = new User[1];
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            userData[0] = documentSnapshot.toObject(User.class);
        }).addOnFailureListener(e -> {
            Log.e("Get User Data", "Error loading user data", e);
        });
        return userData[0];
    }

    public interface OnUserDataLoadedListener {
        void onUserDataLoaded(User user);
        void onUserDataLoadFailed(Exception e);
    }

    public interface OnUpdatedListener {
        void onUpdateSuccess();
        void onUpdateFailed(Exception e);
    }

    /**
     * Takes in a Mood object and the userID of the user that created the mood, and
     * adds the mood to the database with the userID. Creates a unique moodID to track the mood.
     * @param mood A mood object
     * @param uid The userID of the user that created the mood
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
     * Updates and existing mood in firebase with the new mood information.
     * @param moodID Unique ID of the mood to be updated
     * @param updates Map of the updates
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
     * @param mood Specified mood to be deleted
     */
    public void deleteMood(Mood mood) {
        db.collection("moods").document(mood.getMoodID().toString())
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
     * Returns a list of moods created by a user, from userID
     * @param uid UserId of the user that created the moods
     * @return List<Mood> an ArrayList of the moods that a user has created
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
                        userMoods.add(new Mood(moodID, userID, state, postDate, trigger, photo, socialSituation, location));
                    }

                }
            }
        });
        return userMoods;
    }

    /**
     * To be implemented
     */
    public interface OnMoodLoadedListener {
        void onMoodsLoaded(List<Mood> moods);
        void onMoodsLoadedFailed(Exception e);
    }

    /**
     * To be implemented
     * @param follower
     * @param followee
     */
    public void addFollower(User follower, User followee) {
        db.collection("follows").document();
    }

    /**
     * To be implemented
     * @param follower
     * @param followee
     */
    public void removeFollwer(User follower, User followee) {

    }

    /**
     * To be implemented
     * @param follower
     * @param followee
     */
    public void getFollowers(User follower, User followee) {

    }
}
