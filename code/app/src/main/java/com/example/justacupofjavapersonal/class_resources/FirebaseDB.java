package com.example.justacupofjavapersonal.class_resources;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.Write;

import java.util.ArrayList;
import java.util.Collections;
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

    private void fetchUsersFromUid(List<String> userIDs, OnUsersRetrievedListener listener) {
        List<User> userList = new ArrayList<>();

        for (String uid : userIDs) {
            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            userList.add(user);
                        }
                        if (userList.size() == userIDs.size()) {
                            listener.onUsersRetrieved(userList);
                        }
                    });
        }
    }

//    public interface OnUsersRetrievedListener {
//        void onUsersRetrieved(List<User> userList);
//
//        void onUsersRetrievedFailed(Exception e);
//    }

    public void getAllUsers(OnUsersRetrievedListener listener) {
        db.collection("users").get().addOnCompleteListener(task -> {
            List<User> userList = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                }
            }
            listener.onUsersRetrieved(userList);
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
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            User user = documentSnapshot.toObject(User.class);
                            searchedList.add(user);
                        }
                    }
                    listener.onUsersRetrieved(searchedList);
                });
    }

    /**
     *
     */

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
        //mood.setMoodID(moodID); // ✅ set it BEFORE saving
        mood.setUid(uid);       // ✅ and set UID too

        db.collection("moods").document(moodID)
                .set(mood)
                .addOnSuccessListener(aVoid -> Log.d("Add Mood", "Mood stored in DB"))
                .addOnFailureListener(e -> Log.e("Add Mood", "Mood not saved to DB", e));

        // Add user id to a mood to connect it back to the user
        db.collection("moods").document(moodID)
                .update("uid", uid);
        db.collection("moods").document(moodID)
                .update("moodID", moodID);
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
                    String userID = snapshot.getString("uid"); // Get the uid to compare to the user that you want

                    if (userID.equals(uid)) {
                        userMoods.add(snapshot.toObject(Mood.class));
                    }
                }
            }
        });
        return userMoods;
    }

    /**
     * Updates the entire Mood document in Firestore using its moodID.
     * Replaces the document with the latest Mood data.
     * @param mood The Mood object with updated info (must include moodID)
     */
    public void updateMoodInDB(Mood mood) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && mood.getMoodID() != null) {
            db.collection("moods")
                    .document(mood.getMoodID())
                    .set(mood)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FirebaseDB", "Mood updated successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseDB", "Failed to update mood: " + e.getMessage());
                    });
        } else {
            Log.e("FirebaseDB", "Invalid user or moodID. Cannot update mood.");
        }
    }

    /**
     * To be implemented
     */
    public interface OnMoodLoadedListener {
        void onMoodsLoaded(List<Mood> moods);
        void onMoodsLoadedFailed(Exception e);
    }

    /**
     * Adds a following relation to the database.
     * Followee is the one being followed, follower is the one following
     *
     * @param batch
     * @param followerID
     * @param followeeID
     */
    public void addFollower(WriteBatch batch, @NonNull String followerID, @NonNull String followeeID) {
        // Followee is the one being followed, the name of the document
        // Use update so that the old information isn't overwritten
        // Add to collection the users that are following a specified user
        DocumentReference followedByRef = db.collection("followedBy").document(followeeID);
        batch.set(followedByRef,
                Collections.singletonMap("followers", FieldValue.arrayUnion(followerID)),
                SetOptions.merge()
        );

        // Add to the follows collection. The follows collection holds the users the
        // that a specified user is following
        DocumentReference followsRef =  db.collection("follows").document(followerID);
        batch.set(followsRef,
                Collections.singletonMap("following", FieldValue.arrayUnion(followeeID)),
                SetOptions.merge()
        );
    }

    public void sendRequest(String currUserID, String requestedID) {
        //Store the follow requests in a collection "requests"
        // Each document will have a requesteeID
        // The requesteeID document will store all the userIDs of the users
        // that have requested to follow
        WriteBatch batch = db.batch();

        DocumentReference requestRef = db.collection("requests").document(requestedID);
        batch.set(requestRef,
                Collections.singletonMap("requesters", FieldValue.arrayUnion(currUserID)),
                SetOptions.merge()
        );

        // All the users that the current user has requested to follow
        DocumentReference requestedByRef = db.collection("requestedBy").document(currUserID);
        batch.set(requestedByRef,
                Collections.singletonMap("requests", FieldValue.arrayUnion(requestedID)),
                SetOptions.merge()
        );

        batch.commit()
                .addOnSuccessListener(a -> Log.d("FollowRequest", "Request added successfully"))
                .addOnFailureListener(e -> Log.e("FollowRequest", "Request add failure", e));
    }

    public void getAllRequests(String userID, OnUsersRetrievedListener listener) {

        db.collection("requests")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> requesterIds = (List<String>) documentSnapshot.get("requesters");

                        if (requesterIds == null || requesterIds.isEmpty()) {
                            listener.onUsersRetrieved(new ArrayList<>());
                            return;
                        }

                        fetchUsersFromUid(requesterIds, listener);
                    }
                })
                .addOnFailureListener(e -> Log.e("Follower Requests", "Error fetching requests", e));
    }
    public void getAllRequestIds(String userID, OnUserIdsRetrievedListener listener) {
        db.collection("requests")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> requesterIds = (List<String>) documentSnapshot.get("requesters");

                        if (requesterIds == null || requesterIds.isEmpty()) {
                            listener.onUserIdsRetrieved(new ArrayList<>());
                            return;
                        }
                        listener.onUserIdsRetrieved(requesterIds);
                    }
                })
                .addOnFailureListener(e -> Log.e("Follower Requests", "Error fetching requests", e));
    }


    /**
     *  Interface only for retrieving IDs
     */
    public interface OnUserIdsRetrievedListener {
        void onUserIdsRetrieved(List<String> idList);
    }


    /**
     * Retrieves the userids of all the users that the current user has requested to follow
     *
     * @param userID
     * @param listener
     */
    public void getAllRequestedIds(String userID, OnUserIdsRetrievedListener listener) {
        db.collection("requestedBy")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> requesterIds = (List<String>) documentSnapshot.get("requests");
                        Log.d("RequestedIds", "Ids: " + requesterIds);
                        if (requesterIds == null || requesterIds.isEmpty()) {
                            Log.d("Requested Ids", "requesterIds == null");
                            listener.onUserIdsRetrieved(new ArrayList<>());
                            return;
                        }
                        listener.onUserIdsRetrieved(requesterIds);
                    }
                })
                .addOnFailureListener(e -> Log.e("Follower Requests", "Error fetching requests", e));
    }

    /**
     * Retrieves a list of the ids of all users that a user is following
     *
     * @param userID
     * @param listener
     */
    public void getAllFollowingIds(String userID, OnUserIdsRetrievedListener listener) {
        db.collection("follows")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> followingIds = (List<String>) documentSnapshot.get("following");

                        if (followingIds == null || followingIds.isEmpty()) {
                            Log.d("Requested Ids", "requesterIds == null");
                            listener.onUserIdsRetrieved(new ArrayList<>());
                            return;
                        }
                        listener.onUserIdsRetrieved(followingIds);
                    }
                })
                .addOnFailureListener(e -> Log.e("Follower Requests", "Error fetching requests", e));
    }

    public void acceptRequest(String currUserID, String requesterID) {
        WriteBatch batch = db.batch();

        DocumentReference docRef = db.collection("requests").document(currUserID);

        addFollower(batch, currUserID,  requesterID);

        batch.update(docRef, "requesters", FieldValue.arrayRemove(requesterID));

        batch.commit()
                .addOnSuccessListener(a -> Log.d("FollowRequest", "Request accepted successfully"))
                .addOnFailureListener(e -> Log.e("FollowRequest", "Request accept failure",e));
    }

    public void removeRequest(String currUserID, String requesterID) {
        DocumentReference docRef = db.collection("requests").document(currUserID);

        docRef.update("requesters", FieldValue.arrayRemove(requesterID))
                .addOnSuccessListener(a -> Log.d("FollowRequest", "Request rejected success"))
                .addOnFailureListener(e -> Log.e("FollowRequest", "Request rejected failure"));
    }

    /**
     * Removes a user form the current user's following list
     * @param followerId
     * @param followeeId
     */
    public void removeFollowing(String followerId, String followeeId) {
        WriteBatch batch = db.batch();

        DocumentReference followedByRef = db.collection("followedBy").document(followeeId);
        batch.set(followedByRef,
                Collections.singletonMap("followers", FieldValue.arrayRemove(followerId)),
                SetOptions.merge()
        );

        // Add to the follows collection. The follows collection holds the users the
        // that a specified user is following
        DocumentReference followsRef =  db.collection("follows").document(followerId);
        batch.set(followsRef,
                Collections.singletonMap("following", FieldValue.arrayRemove(followeeId)),
                SetOptions.merge()
        );

        batch.commit()
                .addOnSuccessListener(a -> Log.d("Following", "Following removed successfully"))
                .addOnFailureListener(e -> Log.e("Following", "Following remove failure",e));
    }

    /**
     * To be implemented
     * @param currUserID
     */
    public void getFollowing(String currUserID, OnUsersRetrievedListener listener) {
        db.collection("follows")
                .document(currUserID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> followingIds = (List<String>) documentSnapshot.get("following");

                        if (followingIds == null || followingIds.isEmpty()) {
                            listener.onUsersRetrieved(new ArrayList<>());
                            return;
                        }

                        fetchUsersFromUid(followingIds, listener);
                    }
                })
                .addOnFailureListener(e -> Log.e("Get Followering", "Error fetching following", e));
    }
    public interface OnUsersRetrievedListener {
        void onUsersRetrieved(List<User> userList);

        void onUsersRetrievedFailed(Exception e); // Add this if missing
    }

}