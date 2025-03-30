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
import com.google.firebase.firestore.Query;
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

    /**
     * Fetches user details from Firestore based on a list of user IDs.
     *
     * This method retrieves user documents from the "users" collection in Firestore.
     * Each document corresponding to a user ID in the provided list is fetched and converted
     * into a User object. Once all users are retrieved, the result is passed
     * to the provided OnUsersRetrievedListener.
     *
     * @param userIDs   A list of user IDs whose details need to be fetched.
     * @param listener  A callback interface OnUsersRetrievedListener that is triggered
     *                  once all users are retrieved successfully.
     */
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


    /**
     * Retrieves all users from the "users" collection in Firestore.
     *
     * This method fetches all documents from the "users" collection in Firestore,
     * converts each document into a User object, and adds it to a list.
     * Once the retrieval is complete, the list of users is passed to the provided
     * OnUsersRetrievedListener callback.
     *
     * @param listener A callback interface OnUsersRetrievedListener that is triggered
     *                 once all users are retrieved, whether the task is successful or not.
     */
    public void getAllUsers(OnUsersRetrievedListener listener) {
        db.collection("users").get().addOnCompleteListener(task -> {
            List<User> userList = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (auth.getCurrentUser() != null) {
                        if (!user.getUid().equals(auth.getCurrentUser().getUid())) {
                            userList.add(user);
                        }
                    } else {
                        userList.add(user);
                    }
                }
            }
            listener.onUsersRetrieved(userList);
        });
    }

    /**
     * Searches for users in the "users" collection based on a given query.
     *
     * <p>If the search query is empty, this method retrieves all users by calling getAllUsers(OnUsersRetrievedListener).
     * Otherwise, it performs a Firestore query to find users whose names start with the given search string.
     *
     * @param search   The search query string used to filter users by name.
     * @param listener A callback interface OnUsersRetrievedListener that is triggered
     *                 once the search results are retrieved.
     */
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
                            if (auth.getCurrentUser() != null) {
                                if (!user.getUid().equals(auth.getCurrentUser().getUid())) {
                                    searchedList.add(user);
                                }
                            } else {
                                searchedList.add(user);
                            }
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
     * Listener interface for retrieving mood data from Firestore.
     *
     * <p>This interface provides callback methods to handle both successful and failed
     * retrievals of mood data.
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

    /**
     * Sends a follow request from the current user to another user.
     *
     * <p>This method updates two Firestore collections:
     * <ul>
     *     <li>requests: Stores follow requests for each user. The document ID is the requested user's ID,
     *     and it contains an array of user IDs representing users who have sent a follow request.</li>
     *     <li>requestedBy: Stores the follow requests made by the current user. The document ID is the
     *     current user's ID, and it contains an array of user IDs representing users they have requested to follow.</li>
     * </ul>
     *
     * <p>Data is written using a Firestore batch operation to ensure atomicity.
     *
     * @param currUserID   The ID of the current user sending the follow request.
     * @param requestedID  The ID of the user receiving the follow request.
     */
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

    /**
     * Retrieves all follow requests for a given user.
     *
     * <p>This method fetches the list of user IDs who have sent a follow request to the specified user
     * from the "requests" collection in Firestore. If there are requesters, their user details are
     * retrieved using fetchUsersFromUid(List, OnUsersRetrievedListener).
     *
     * @param userID   The ID of the user whose follow requests are being retrieved.
     * @param listener A callback interface OnUsersRetrievedListener that receives the list of users
     *                 who have sent follow requests.
     */
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

    /**
     * Retrieves all follow request IDs for a given user.
     *
     * <p>This method fetches the list of user IDs who have sent a follow request to the specified user
     * from the "requests" collection in Firestore. The retrieved user IDs are then passed to the provided
     * OnUserIdsRetrievedListener callback.
     *
     * @param userID   The ID of the user whose follow request IDs are being retrieved.
     * @param listener A callback interface OnUserIdsRetrievedListener that receives the list of user IDs
     *                 who have sent follow requests.
     */
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
     * Listener interface for retrieving a list of user IDs from Firestore.
     *
     * <p>This interface provides a callback method to handle the retrieval of user IDs,
     * typically used for fetching follow request IDs or other user-related data.
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

    /**
     * Accepts a follow request from a requester and updates Firestore accordingly.
     *
     * <p>This method performs the following operations using a Firestore batch write:
     * <ul>
     *     <li>Adds the requester as a follower of the current user by calling addFollower(WriteBatch, String, String).</li>
     *     <li>Removes the requester ID from the "requesters" array in the "requests" collection.</li>
     * </ul>
     *
     * @param currUserID   The ID of the current user accepting the follow request.
     * @param requesterID  The ID of the user whose follow request is being accepted.
     */
    public void acceptRequest(String currUserID, String requesterID) {
        WriteBatch batch = db.batch();

        DocumentReference docRef = db.collection("requests").document(currUserID);

        addFollower(batch, currUserID,  requesterID);

        batch.update(docRef, "requesters", FieldValue.arrayRemove(requesterID));

        batch.commit()
                .addOnSuccessListener(a -> Log.d("FollowRequest", "Request accepted successfully"))
                .addOnFailureListener(e -> Log.e("FollowRequest", "Request accept failure",e));
    }

    /**
     * Removes a follow request from the current user's request list.
     *
     * <p>This method updates the "requests" collection in Firestore by removing the requester ID
     * from the "requesters" array of the specified user.
     *
     * @param currUserID   The ID of the current user rejecting the follow request.
     * @param requesterID  The ID of the user whose follow request is being removed.
     */
    public void removeRequest(String currUserID, String requesterID) {
        WriteBatch batch = db.batch();

        DocumentReference requestsRef = db.collection("requests").document(currUserID);
        DocumentReference requestedByRef = db.collection("requestedBy").document(requesterID);

        batch.set(requestsRef,
                Collections.singletonMap("requesters", FieldValue.arrayRemove(requesterID)),
                SetOptions.merge()
        );

        batch.set(requestedByRef,
                Collections.singletonMap("requests", FieldValue.arrayRemove(currUserID)),
                SetOptions.merge()
        );

        batch.commit()
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
     * Retrieves a list of users that the current user is following.
     *
     * <p>This method fetches the list of user IDs that the specified user is following
     * from the "follows" collection in Firestore. If the current user is following any users,
     * their details are retrieved using fetchUsersFromUid(List, OnUsersRetrievedListener).
     *
     * @param currUserID   The ID of the user whose following list is being retrieved.
     * @param listener     A callback interface OnUsersRetrievedListener that receives the list of users
     *                     the current user is following.
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

    public void getUserMoods(String uid, OnMoodLoadedListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        CollectionReference moodsRef = db.collection("moods");

        Query query;
        if (uid.equals(currentUser.getUid())) {
            // Show all moods (private and public) for the current user
            query = moodsRef.whereEqualTo("uid", uid);
        } else {
            // Show only public moods for other users
            query = moodsRef.whereEqualTo("uid", uid)
                    .whereEqualTo("privacy", "Public");
        }

        query.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Mood> moodList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Mood mood = doc.toObject(Mood.class);
                        if (mood != null) {
                            moodList.add(mood);
                        }
                    }
                    listener.onMoodsLoaded(moodList);
                })
                .addOnFailureListener(e -> listener.onMoodsLoadedFailed(e));
    }
//    public interface OnMoodLoadedListener {
//        void onMoodsLoaded(List<Mood> moods);
//        void onMoodsLoadedFailed(Exception e);
//    }


}