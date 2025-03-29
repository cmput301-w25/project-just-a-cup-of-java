package com.example.justacupofjavapersonal.class_resources;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class FirebaseDB {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public interface OnUpdatedListener {
        void onUpdateSuccess();
        void onUpdateFailed(Exception e);
    }

    public interface OnUsersRetrievedListener {
        void onUsersRetrieved(List<User> userList);
        void onUsersRetrievedFailed(Exception e);
    }

    public interface OnUserIdsRetrievedListener {
        void onUserIdsRetrieved(List<String> idList);
    }

    public interface OnMoodLoadedListener {
        void onMoodsLoaded(List<Mood> moods);
        void onMoodsLoadedFailed(Exception e);
    }

    public void updateUser(String uid, Map<String, Object> updates, OnUpdatedListener listener) {
        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> listener.onUpdateSuccess())
                .addOnFailureListener(e -> listener.onUpdateFailed(e));

        FirebaseUser user = auth.getCurrentUser();
        if (updates.containsKey("email") && user != null) {
            user.updateEmail(updates.get("email").toString());
        }
        if (updates.containsKey("password") && user != null) {
            user.updatePassword(updates.get("password").toString());
        }
    }

    public User getUser(String uid) {
        final User[] userData = new User[1];
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> userData[0] = doc.toObject(User.class))
                .addOnFailureListener(e -> Log.e("Get User", "Failed", e));
        return userData[0];
    }

    public void getAllUsers(OnUsersRetrievedListener listener) {
        db.collection("users").get().addOnCompleteListener(task -> {
            List<User> userList = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    userList.add(doc.toObject(User.class));
                }
                listener.onUsersRetrieved(userList);
            } else {
                listener.onUsersRetrievedFailed(task.getException());
            }
        });
    }

    public void searchUsers(String search, OnUsersRetrievedListener listener) {
        if (search.isEmpty()) {
            getAllUsers(listener);
            return;
        }

        db.collection("users")
                .orderBy("name")
                .startAt(search)
                .endAt(search + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    List<User> searchedList = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            searchedList.add(doc.toObject(User.class));
                        }
                        listener.onUsersRetrieved(searchedList);
                    } else {
                        listener.onUsersRetrievedFailed(task.getException());
                    }
                });
    }

    public void addMoodtoDB(Mood mood, String uid) {
        String moodID = db.collection("moods").document().getId();
        mood.setMoodID(moodID);
        mood.setUid(uid);

        db.collection("moods").document(moodID)
                .set(mood)
                .addOnSuccessListener(aVoid -> Log.d("Add Mood", "Mood stored"))
                .addOnFailureListener(e -> Log.e("Add Mood", "Failed", e));
    }

    public void updateMood(String moodID, Map<String, Object> updates, OnUpdatedListener listener) {
        db.collection("moods").document(moodID)
                .update(updates)
                .addOnSuccessListener(aVoid -> listener.onUpdateSuccess())
                .addOnFailureListener(e -> listener.onUpdateFailed(e));
    }

    public void updateMoodInDB(Mood mood) {
        if (mood.getMoodID() == null) return;
        db.collection("moods").document(mood.getMoodID())
                .set(mood)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseDB", "Mood updated"))
                .addOnFailureListener(e -> Log.e("FirebaseDB", "Mood update failed", e));
    }

    public void deleteMood(Mood mood) {
        db.collection("moods").document(mood.getMoodID())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete Mood", "Success"))
                .addOnFailureListener(e -> Log.w("Delete Mood", "Failed", e));
    }

    public void getUserMoods(String uid, OnMoodLoadedListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        Query query = db.collection("moods").whereEqualTo("uid", uid);
        if (!uid.equals(currentUser.getUid())) {
            query = query.whereEqualTo("privacy", "Public");
        }

        query.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Mood> moods = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        Mood mood = doc.toObject(Mood.class);
                        if (mood != null) moods.add(mood);
                    }
                    listener.onMoodsLoaded(moods);
                })
                .addOnFailureListener(listener::onMoodsLoadedFailed);
    }

    public void addFollower(WriteBatch batch, String followerID, String followeeID) {
        DocumentReference followedByRef = db.collection("followedBy").document(followeeID);
        DocumentReference followsRef = db.collection("follows").document(followerID);

        batch.set(followedByRef, Collections.singletonMap("followers", FieldValue.arrayUnion(followerID)), SetOptions.merge());
        batch.set(followsRef, Collections.singletonMap("following", FieldValue.arrayUnion(followeeID)), SetOptions.merge());
    }

    public void sendRequest(String currUserID, String requestedID) {
        WriteBatch batch = db.batch();

        DocumentReference requestRef = db.collection("requests").document(requestedID);
        DocumentReference requestedByRef = db.collection("requestedBy").document(currUserID);

        batch.set(requestRef, Collections.singletonMap("requesters", FieldValue.arrayUnion(currUserID)), SetOptions.merge());
        batch.set(requestedByRef, Collections.singletonMap("requests", FieldValue.arrayUnion(requestedID)), SetOptions.merge());

        batch.commit()
                .addOnSuccessListener(a -> Log.d("Request", "Sent"))
                .addOnFailureListener(e -> Log.e("Request", "Failed", e));
    }

    public void getAllRequests(String userID, OnUsersRetrievedListener listener) {
        db.collection("requests").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> ids = (List<String>) doc.get("requesters");
                    if (ids == null || ids.isEmpty()) {
                        listener.onUsersRetrieved(new ArrayList<>());
                    } else {
                        fetchUsersFromUid(ids, listener);
                    }
                })
                .addOnFailureListener(e -> listener.onUsersRetrievedFailed(e));
    }

    public void getAllRequestIds(String userID, OnUserIdsRetrievedListener listener) {
        db.collection("requests").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> ids = (List<String>) doc.get("requesters");
                    listener.onUserIdsRetrieved(ids != null ? ids : new ArrayList<>());
                });
    }

    public void getAllRequestedIds(String userID, OnUserIdsRetrievedListener listener) {
        db.collection("requestedBy").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> ids = (List<String>) doc.get("requests");
                    listener.onUserIdsRetrieved(ids != null ? ids : new ArrayList<>());
                });
    }

    public void acceptRequest(String currUserID, String requesterID) {
        WriteBatch batch = db.batch();
        DocumentReference reqRef = db.collection("requests").document(currUserID);
        batch.update(reqRef, "requesters", FieldValue.arrayRemove(requesterID));
        addFollower(batch, currUserID, requesterID);

        batch.commit()
                .addOnSuccessListener(a -> Log.d("FollowRequest", "Accepted"))
                .addOnFailureListener(e -> Log.e("FollowRequest", "Failed", e));
    }

    public void removeRequest(String currUserID, String requesterID) {
        DocumentReference docRef = db.collection("requests").document(currUserID);
        docRef.update("requesters", FieldValue.arrayRemove(requesterID));
    }

    public void getFollowing(String userID, OnUsersRetrievedListener listener) {
        db.collection("follows").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> ids = (List<String>) doc.get("following");
                    if (ids == null || ids.isEmpty()) {
                        listener.onUsersRetrieved(new ArrayList<>());
                    } else {
                        fetchUsersFromUid(ids, listener);
                    }
                })
                .addOnFailureListener(listener::onUsersRetrievedFailed);
    }

    private void fetchUsersFromUid(List<String> userIDs, OnUsersRetrievedListener listener) {
        List<User> userList = new ArrayList<>();
        for (String uid : userIDs) {
            db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    userList.add(doc.toObject(User.class));
                    if (userList.size() == userIDs.size()) {
                        listener.onUsersRetrieved(userList);
                    }
                }
            });
        }
    }

    public void removeFollower(User follower, User followee) {
        // Optional implementation
    }
}
