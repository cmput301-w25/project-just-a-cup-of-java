package com.example.justacupofjavapersonal.ui.comments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

//java docs code
/**
 * Represents a comment in a mood.
 * @param userId The ID of the user who wrote the comment.
 * @return The ID of the user who wrote the comment.
 *
 */

public class CommentBottomSheet extends BottomSheetDialogFragment {
    private String moodId;
    private FirebaseDB db;
    private RecyclerView recyclerView;
    private EditText commentInput;
    private Button postButton;
    private CommentAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();

    public CommentBottomSheet(String moodId) {
        this.moodId = moodId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false);

        recyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentInput = view.findViewById(R.id.commentInput);
        postButton = view.findViewById(R.id.postCommentButton);
        db = new FirebaseDB();

        adapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadComments();

        postButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            Log.d("COMMENTS", "Posting comment: " + commentText);

            if (!commentText.isEmpty()) {
                db.addCommentToMood(moodId, commentText);
                commentInput.setText("");
                loadComments(); // Refresh
            }
        });


        return view;
    }

    private void loadComments() {
        FirebaseFirestore.getInstance().collection("moods")
                .document(moodId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    commentList.clear();
                    for (DocumentSnapshot doc : querySnapshots) {
                        Comment comment = doc.toObject(Comment.class);
                        commentList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
