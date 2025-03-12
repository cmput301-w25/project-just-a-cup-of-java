package com.example.justacupofjavapersonal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChangePasswordTest {
    private ActivityScenario<MainActivity> activityScenario;
    private String testEmail;
    private static final String INITIAL_PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newpassword456";

    @BeforeClass
    public static void setup(){

        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
    }

    @Before
    public void setUp() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
    }



    @Test
    public void testSignupChangePasswordAndLoginFlow() throws InterruptedException {
        testEmail = "test+" + System.currentTimeMillis() + "@example.com";
        onView(withId(R.id.emailEditText))
                .perform(replaceText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(replaceText(INITIAL_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordEditText))
                .perform(replaceText(INITIAL_PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.signupButton)).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_username)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_email)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_bio)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_change_password)).perform(click());

        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmPasswordEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.changePasswordButton)).check(matches(isDisplayed()));

        onView(withId(R.id.passwordEditText))
                .perform(replaceText(NEW_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordEditText))
                .perform(replaceText(NEW_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.changePasswordButton)).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.back_arrow)).perform(click());
        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_logout)).perform(click());

        Thread.sleep(2000);

        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.emailEditText))
                .perform(replaceText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(replaceText(INITIAL_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(5000);


        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.emailEditText))
                .perform(replaceText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(replaceText(NEW_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click()); // Adjust ID

        Thread.sleep(3000);  //gives time to visually make sure it's in the home fragment


    }


//    @After  //I want to be able to see the data in the emulator for now so commented out the cleanup
//    public void tearDown() {
//        String projectId = "com.example.justacupofjavapersonal;";
//        URL url = null;
//        try {
//            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
//        } catch (MalformedURLException exception) {
//            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
//        }
//        HttpURLConnection urlConnection = null;
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("DELETE");
//            int response = urlConnection.getResponseCode();
//            Log.i("Response Code", "Response Code: " + response);
//        } catch (IOException exception) {
//            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//    }
}