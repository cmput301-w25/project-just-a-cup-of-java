package com.example.just_a_cup_of_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a button or UI element that navigates to AddMood
        Button addMoodButton = findViewById(R.id.addMoodButton); // Ensure this ID exists in activitymain.xml

        // Set click listener to open AddMood activity
        addMoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMood.class);
                startActivity(intent);
            }
        });
    }
}
