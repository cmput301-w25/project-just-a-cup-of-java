package com.example.justacupofjavapersonal;

import android.view.View;

import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class RecyclerViewTestHelper {

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified ID.";
            }

            @Override
            public void perform(androidx.test.espresso.UiController uiController, View view) {
                View childView = view.findViewById(id);
                if (childView != null) {
                    childView.performClick();
                }
            }
        };
    }
}
