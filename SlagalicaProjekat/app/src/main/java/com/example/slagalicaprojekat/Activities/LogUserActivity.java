package com.example.slagalicaprojekat.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slagalicaprojekat.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LogUserActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://slagalica-3c783-default-rtdb.firebaseio.com/");

    private Button playButton, rangListButton;

    private String loggedInUsername = ""; // Track logged-in user

    private DatabaseReference requestsDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_user);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        int zvezde = sharedPreferences.getInt("zvezde", 0);
        int tokeni = sharedPreferences.getInt("tokeni", 0);

        TextView zvezdeTextView = findViewById(R.id.zvezde);
        TextView tokeniTextView = findViewById(R.id.tokeni);

        zvezdeTextView.setText("Zvezde: " + zvezde);
        tokeniTextView.setText("Tokeni: " + tokeni);

        Button btn1 = findViewById(R.id.profile);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogUserActivity.this, MyProfileActivity.class));
            }
        });

        playButton = findViewById(R.id.btn_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndConsumeToken()) {
                    startActivity(new Intent(LogUserActivity.this, Game3Activity.class));
                } else {
                    Toast.makeText(LogUserActivity.this, "Nemate dovoljno tokena.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rangListButton = findViewById(R.id.btn_rang_list);
        rangListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogUserActivity.this, RangListaActivity.class));
            }
        });

    }

    private boolean checkAndConsumeToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        int currentTokens = sharedPreferences.getInt("tokeni", 0);
        int currentZvezde = sharedPreferences.getInt("zvezde", 0);
        String usernameTxt = sharedPreferences.getString("username", "");

        if (currentTokens > 0) {
            // Ako korisnik ima dovoljno tokena, smanjite broj tokena za 1
            currentTokens--;
            databaseReference.child("users").child(usernameTxt).child("tokeni").setValue(currentTokens);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("tokeni", currentTokens);
            editor.putInt("zvezde", currentZvezde);
            editor.apply();

            TextView tokeniTextView = findViewById(R.id.tokeni);
            tokeniTextView.setText("Tokeni: " + currentTokens);

            TextView zvezdeTextView = findViewById(R.id.zvezde);
            zvezdeTextView.setText("Zvezde: " + currentZvezde);

            return true;
        } else {
            // Ako korisnik nema dovoljno tokena, vrati false
            return false;
        }
    }

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
