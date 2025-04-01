package com.example.justacupofjavapersonal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.justacupofjavapersonal.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AllFriendsFollowFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void testLoginUser() throws InterruptedException {
        Thread.sleep(3000);

        onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(3000);

        onView(withId(R.id.emailEditText)).perform(replaceText("tester3@test.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(replaceText("123456"), closeSoftKeyboard());

        Thread.sleep(3000);

        onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(3000);

        onView(withId(R.id.navigation_notifications)).perform(click());
    }

    @Test
    public void testRecyclerViewIsDisplayed() throws InterruptedException {
        Thread.sleep(5000);
        onView(withId(R.id.all_users_recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchingUser() throws InterruptedException {
        Thread.sleep(5000);
        onView(withId(R.id.user_search)).perform(replaceText("test3"), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());
        // Check that it shows the name "test3"
    }

    @Test
    public void testNavigationToFollowerRequests() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.follower_requests_friends)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.follower_requests_fragment)).check(matches(isDisplayed()));
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Test
    public void testFollowRequest() {
        onView(withId(R.id.all_users_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, RecyclerViewTestHelper.clickChildViewWithId(R.id.follow_button)));

        onView(withRecyclerView(R.id.all_users_recyclerView)
                .atPositionOnView(0, R.id.follow_button))
                .check(matches(withText("Requested")));
    }

    @Test
    public void testRemoveRequest() {
        onView(withId(R.id.all_users_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, RecyclerViewTestHelper.clickChildViewWithId(R.id.follow_button)));

        onView(withRecyclerView(R.id.all_users_recyclerView)
                .atPositionOnView(0, R.id.follow_button))
                .check(matches(withText("Follow")));
    }
}
