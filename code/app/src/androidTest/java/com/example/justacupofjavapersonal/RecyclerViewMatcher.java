package com.example.justacupofjavapersonal;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class RecyclerViewMatcher {
    private final int recyclerViewId;

    public RecyclerViewMatcher(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView item at position " + position);
            }

            @Override
            protected boolean matchesSafely(View item) {
                RecyclerView recyclerView = item.getRootView().findViewById(recyclerViewId);
                if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                    View childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                    View targetView = childView.findViewById(targetViewId);
                    return targetView != null;
                }
                return false;
            }
        };
    }
}
