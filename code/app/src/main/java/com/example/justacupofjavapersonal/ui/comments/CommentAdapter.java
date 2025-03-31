package com.example.justacupofjavapersonal.ui.comments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, text;

        public CommentViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.commentUserName);
            text = itemView.findViewById(R.id.commentText);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.userName.setText(comment.getUserName());
        holder.text.setText(comment.getText());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
