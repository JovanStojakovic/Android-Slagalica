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
    private int odigranePartije = 0;
    private int tokens = 0;
    private TextView finalScore;
    private int bodoviZaKoZnaZna;
    private int bodoviZaKorakPoKorak;
    private int bodoviZaMojBroj;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://slagalica-3c783-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kraj_igre);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String usernameTxt = sharedPreferences.getString("username", "");

        finalScore = findViewById(R.id.osvojeniBodovi);

        // Učitavanje postojećih vrednosti iz SharedPreferences
        odigranePartije = sharedPreferences.getInt("odigranePartije", 0);
        stars = sharedPreferences.getInt("zvezde", 0);

        score = 0;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ukupno-osvojeni-bodovi") && intent.hasExtra("igra1") && intent.hasExtra("igra2") && intent.hasExtra("igra3")) {
            bodoviZaKoZnaZna = intent.getIntExtra("igra1", 0);
            bodoviZaKorakPoKorak = intent.getIntExtra("igra2", 0);
            bodoviZaMojBroj = intent.getIntExtra("igra3", 0);
            int ukupnoBodova = intent.getIntExtra("ukupno-osvojeni-bodovi", 0);
            score += ukupnoBodova;

            stars += 10 + score / 40;
            odigranePartije++;

            // Ažuriranje vrednosti u SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("odigranePartije", odigranePartije);
            editor.putInt("zvezde", stars);
            editor.apply();

            // Ažuriranje vrednosti u Firebase bazi podataka
            databaseReference.child("users").child(usernameTxt).child("zvezde").setValue(stars);
            databaseReference.child("users").child(usernameTxt).child("odigranePartije").setValue(odigranePartije);
        }

        if (isUserLoggedIn()) {
            finalScore.setText("Ukupno ste osvojili: " + score + " bodova\n");
            finalScore.append("U igri 'Ko zna zna' ste ostvarili: " + bodoviZaKoZnaZna + " od 50 bodova!\n");
            finalScore.append("U igri 'Korak po korak' ste ostvarili: " + bodoviZaKorakPoKorak + " od 20 bodova!\n");
            finalScore.append("U igri 'Moj broj' ste ostvarili: " + bodoviZaMojBroj + " od 20 bodova!\n");
            finalScore.append("Ukupan broj zvezdi: " + stars );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("odigranePartije", odigranePartije);
            editor.putInt("zvezde", stars);

            // Dodavanje tokena svakih 50 zvezda
            int prethodneZvezde = sharedPreferences.getInt("prethodneZvezde", 0);
            if ((stars / 50) > (prethodneZvezde / 50)) {
                tokens++; // Dodajte jedan token
                editor.putInt("tokeni", tokens);
                databaseReference.child("users").child(usernameTxt).child("tokeni").setValue(tokens);
            }

            // Ažuriranje vrednosti u Firebase bazi podataka
            databaseReference.child("users").child(usernameTxt).child("zvezde").setValue(stars);
            databaseReference.child("users").child(usernameTxt).child("odigranePartije").setValue(odigranePartije);
            editor.putInt("prethodneZvezde", stars); // Čuvanje vrednosti zvezda za sledeću proveru
            editor.apply();
        } else {
            // Ako korisnik nije ulogovan, prikaži samo ukupne bodove
            finalScore.setText("Niste prijavljeni.\n Ukupno ste osvojili: " + score + " bodova\n");
        }

        Button krajButton = findViewById(R.id.krajButton);

        // Prikazi odgovarajuci ekran zavisno od toga da li je korisnik ulogovan
        if (isUserLoggedIn()) {
            krajButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(KrajIgreActivity.this, LogUserActivity.class));
                    finish();
                }
            });
        } else {
            krajButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(KrajIgreActivity.this, MainActivity.class));
                    finish();
                }
            });
        }

    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        return sharedPreferences.contains("username");
    }
}
