package com.example.slagalicaprojekat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slagalicaprojekat.R;
import com.google.firebase.database.DatabaseReference;


public class LogUserActivity extends AppCompatActivity {
    private Button playButton;


    private String loggedInUsername = ""; // Track logged-in user

    private DatabaseReference requestsDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_user);

        Button btn1 = findViewById(R.id.profile);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogUserActivity.this,MyProfileActivity.class));
            }
        });


    /*public void openGameActivity() {
        Intent intent = new Intent(this, Game3Activity.class);
        intent.putExtra("loggedInUsername", loggedInUsername); // Pass the username
        startActivity(intent);
    }*/


    /*public void openProfileActivity() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        intent.putExtra("loggedInUsername", loggedInUsername); // Pass the username
        startActivity(intent);
    }*/


    }
}