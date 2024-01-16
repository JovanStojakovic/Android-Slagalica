package com.example.slagalicaprojekat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.slagalicaprojekat.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class KrajIgreActivity extends AppCompatActivity {

    private int score = 0;
    private int stars = 0;
    private TextView finalScore;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://slagalica-3c783-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kraj_igre);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String usernameTxt = sharedPreferences.getString("username", "");

        finalScore = findViewById(R.id.osvojeniBodovi);

        score = 0;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ukupno-osvojeni-bodovi")) {
            int ukupnoBodova = intent.getIntExtra("ukupno-osvojeni-bodovi", 0);
            score += ukupnoBodova;

            stars = 10 + score / 40;

            databaseReference.child("users").child(usernameTxt).child("zvezde").setValue(stars);

        }

        finalScore.setText("Ukupno ste osvojili: " + score + " bodova\nDobili ste: " + stars + " zvezdica");

        Button krajButton = findViewById(R.id.krajButton);

        if (isUserLoggedIn()) {
            krajButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(KrajIgreActivity.this, LogUserActivity.class));
                }
            });
        } else {
            krajButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(KrajIgreActivity.this, MainActivity.class));
                }
            });
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        return sharedPreferences.contains("username");
    }
}