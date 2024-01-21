package com.example.slagalicaprojekat.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slagalicaprojekat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LogUserActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://slagalica-3c783-default-rtdb.firebaseio.com/");
    private Button playButton, rangListButton;
    private String loggedInUsername = ""; // Track logged-in user
    private DatabaseReference requestsDatabase;
    private static final String PREF_NAME = "token_prefs";
    private static final String LAST_TOKEN_TIME = "last_token_time";
    private static final String TOKEN_COUNT = "token_count";
    private static final long TOKENS_GIFT_INTERVAL = 2 * 60 * 1000; // 2 min u milisekundama


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

        String username = sharedPreferences.getString("username", "");
        databaseReference.child("users").child(username).child("tokeni").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long currentTokens = snapshot.getValue(Long.class);
                    // Ažurirajte prikaz tokena u korisničkom interfejsu
                    TextView tokeniTextView = findViewById(R.id.tokeni);
                    tokeniTextView.setText("Tokeni: " + currentTokens);

                    // Sačuvajte vrednost tokena i u SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("tokeni", (int) currentTokens);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handlujte greške
            }
        });

        // Dobijanje vremena kada su tokeni poslednji put dodati
        SharedPreferences tokenPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        long lastTokenTime = tokenPrefs.getLong(LAST_TOKEN_TIME, 0);

        // Trenutno vreme
        long currentTime = System.currentTimeMillis();

        // Provera da li je prošlo više od 24 sata od poslednjeg dodavanja tokena
        if (currentTime - lastTokenTime >= TOKENS_GIFT_INTERVAL) {
            // Pribavi trenutnu vrednost tokena iz SharedPreferences
            int currentTokenCount = tokenPrefs.getInt(TOKEN_COUNT, 0);

            // Dodaj 5 tokena korisniku
            int newTokenCount = currentTokenCount + 5;

            // Ažuriraj vrednost tokena u SharedPreferences
            SharedPreferences.Editor editor = tokenPrefs.edit();
            editor.putLong(LAST_TOKEN_TIME, currentTime);
            editor.putInt(TOKEN_COUNT, newTokenCount);
            editor.apply();

            // Ažuriraj vrednost tokena u bazi podataka
            databaseReference.child("users").child(username).child("tokeni").setValue(newTokenCount)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Ako je uspešno ažurirano u bazi podataka, ažurirajte prikaz u korisničkom interfejsu
                            tokeniTextView.setText("Tokeni: " + newTokenCount);

                            // Sada možete obavestiti korisnika da su mu dodati tokeni
                            Toast.makeText(LogUserActivity.this, "Dobili ste 5 tokena!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Ako nije uspelo ažuriranje u bazi podataka, obavestite korisnika o grešci
                            Toast.makeText(LogUserActivity.this, "Greška pri ažuriranju tokena u bazi podataka.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            // Ako nije prošlo 1 minut, korisnik već ima dovoljno tokena
        }

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
