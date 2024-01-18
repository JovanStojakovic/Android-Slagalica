package com.example.slagalicaprojekat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.slagalicaprojekat.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        int zvezde = intent.getIntExtra("zvezde", 0);
        String email = intent.getStringExtra("email");
        int odigranePartije = intent.getIntExtra("odigranePartije", 0);

        // Prikazivanje informacija o korisniku u UI elementima
        TextView emailTextView = findViewById(R.id.tvEmail);
        TextView usernameTextView = findViewById(R.id.tvUsername);
        TextView zvezdeTextView = findViewById(R.id.tvStars);
        TextView odigranePartijeTextView = findViewById(R.id.tvOdigranePartije);

        usernameTextView.setText("Username: " + username);
        emailTextView.setText("Email: " + email);
        zvezdeTextView.setText("Zvezde: " + zvezde);
        odigranePartijeTextView.setText("Broj odigranih partija: " + odigranePartije);

    }
}
