package com.example.slagalicaprojekat.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slagalicaprojekat.R;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);



        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        String zvezde = sharedPreferences.getString("zvezde", "");

        // Prikazi email i username
        TextView usernameTextView = findViewById(R.id.tv_name);
        TextView emailTextView = findViewById(R.id.tv_email);
        TextView zvezdeTextView = findViewById(R.id.tv_stars);

        usernameTextView.setText("Username: " + username);
        emailTextView.setText("Email: " + email);
        zvezdeTextView.setText("Zvezde: " + zvezde);


        Button btn1 = findViewById(R.id.pocetna_strana);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this,LogUserActivity.class));
            }
        });
        Button logoutButton = findViewById(R.id.odjava);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // brisnje podataka o prijavljenom korisniku iz SharedPreferences-a
                SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.remove("email");
                editor.remove("zvezde");
                editor.apply();
                startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}