package com.example.slagalicaprojekat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slagalicaprojekat.Model.KorakPoKorak;
import com.example.slagalicaprojekat.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game4Activity extends AppCompatActivity {
    private Button answerButton;
    private TextView timerView;
    private boolean gamePaused = false;
    private KorakPoKorak currentRound;
    private int currentStep;
    private int score;
    private int bodoviZaOdgovor = 0;
    private int bodoviIzKoZnaZna;
    private CountDownTimer timer;
    private DatabaseReference databaseReference;
    private List<KorakPoKorak> koraciList = new ArrayList<>();
    private TextView korak1, korak2, korak3, korak4, korak5, korak6, korak7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("KorakPoKorak");

        korak1 = findViewById(R.id.term1TextView);
        korak2 = findViewById(R.id.term2TextView);
        korak3 = findViewById(R.id.term3TextView);
        korak4 = findViewById(R.id.term4TextView);
        korak5 = findViewById(R.id.term5TextView);
        korak6 = findViewById(R.id.term6TextView);
        korak7 = findViewById(R.id.term7TextView);

        answerButton = findViewById(R.id.submitButton);
        timerView = findViewById(R.id.timerTextView2);

        initializeGame();
        updateStep();
        startTimer();

        score = 0;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ukupno-osvojeni-bodovi")) {
            bodoviIzKoZnaZna = intent.getIntExtra("ukupno-osvojeni-bodovi", 0);
            score += bodoviIzKoZnaZna;
        }

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void initializeGame() {
        fetchDataFromFirebase();
    }

    private Handler handler = new Handler();
    private void updateStep() {
        if (currentRound != null) {
            // Postavljanje teksta za korak 1
            korak1.setText(currentRound.getKorak1());

            // Postavljanje teksta za korake 2-7 sa kašnjenjem od 10 sekundi
            postaviKorakRekuzivno(2);
        }
    }

    private void postaviKorakRekuzivno(final int korakIndex) {
        if (korakIndex <= 7) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    postaviKorak(korakIndex);
                    postaviKorakRekuzivno(korakIndex + 1);
                }
            }, 10000); // Kašnjenje od 10 sekundi između koraka
        }
    }

    private void postaviKorak(int korakIndex) {
        switch (korakIndex) {
            case 2:
                korak2.setText(currentRound.getKorak2());
                break;
            case 3:
                korak3.setText(currentRound.getKorak3());
                break;
            case 4:
                korak4.setText(currentRound.getKorak4());
                break;
            case 5:
                korak5.setText(currentRound.getKorak5());
                break;
            case 6:
                korak6.setText(currentRound.getKorak6());
                break;
            case 7:
                korak7.setText(currentRound.getKorak7());
                break;
        }
    }


    private void fetchDataFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    KorakPoKorak korak = snapshot.getValue(KorakPoKorak.class);
                    if (korak != null) {
                        koraciList.add(korak);
                    }
                }

                // Dodajte logove da biste pratili tok podataka
                Log.d("KorakPoKorakActivity", "Broj koraka iz baze: " + koraciList.size());

                // Ako postoje koraci, prikaži prvi korak
                if (!koraciList.isEmpty()) {
                    currentRound = koraciList.get(0);

                    // Dodajte log da biste pratili vrednosti iz currentRound
                    Log.d("KorakPoKorakActivity", "Prvi korak iz baze: " + currentRound.getKorak1());

                    updateStep();
                    startTimer();
                } else {
                    // Dodajte log ako lista koraka iz baze nije popunjena
                    Log.d("KorakPoKorakActivity", "Lista koraka iz baze je prazna.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Greška pri dohvatanju podataka
                Toast.makeText(Game4Activity.this, "Greška u dohvatu podataka", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel(); // Prekini prethodni tajmer ako postoji
        }

        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerView.setText("Vreme: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                // Provera da li je igra pauzirana pre nego što prikažemo sledeći korak
                if (!gamePaused) {
                    moveToNextStep();
                }
            }
        }.start();
    }


    private void moveToNextStep() {
        // Povećavanje trenutnog koraka
        currentStep++;

        if (currentStep > 7) {
            // Ako je trenutni korak 7, prikaži pauseGame
            pauseGame();
            // Kraj igre
            endGame();
        } else {
            // Ako nije kraj igre, ažuriraj korak
            updateStep();
            // Pokreni tajmer za sledeći korak
            startTimer();
        }
    }

    private void checkAnswer() {
        if (timer != null) {
            timer.cancel();
        }

        // Logika za proveru odgovora
        // Ovde možete povećati ili smanjiti bodove, preći na sledeći korak, itd.

        if (jeTacanOdgovor()) {
            // Ako je odgovor tačan, računamo bodove na osnovu trenutnog koraka
            if (currentStep == 0) {
                bodoviZaOdgovor = 20;
            } if (currentStep == 1) {
                bodoviZaOdgovor = 18;
            } if (currentStep == 2) {
                bodoviZaOdgovor = 16;
            } if (currentStep == 3) {
                bodoviZaOdgovor = 14;
            } if (currentStep == 4) {
                bodoviZaOdgovor = 12;
            } if (currentStep == 5) {
                bodoviZaOdgovor = 10;
            } if (currentStep == 6) {
                bodoviZaOdgovor = 8;
            }
        } else {
            // Ako je odgovor netačan, dodajemo 0 bodova
            bodoviZaOdgovor = 0;
        }

        // Dodavanje bodova
        score += bodoviZaOdgovor;

        // Pauziranje igre i prikaz rešenja
        pauseGame();
    }

    private void pauseGame() {
        gamePaused = true;

        // Prikazivanje dijaloga sa rešenjem
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rešenje");
        builder.setMessage("Tačno rešenje je: " + currentRound.getResenje() + "\n" +
                "Osvojili ste " + bodoviZaOdgovor + " bodova u ovoj igri. " + "\n" +
                "Ukupno osvojeni bodovi do sada: " + score + " bodova.");
        builder.setPositiveButton("Nastavi na sledeću igru", (dialog, which) -> {
            Intent intent = new Intent(Game4Activity.this, Game6Activity.class);
            intent.putExtra("igra1", bodoviIzKoZnaZna);
            intent.putExtra("igra2", bodoviZaOdgovor);
            intent.putExtra("ukupno-osvojeni-bodovi", score );
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false); // Onemogućava zatvaranje dijaloga klikom van njega
        builder.show();
    }


    private void resumeGame() {
        gamePaused = false;
        EditText answerEditText = findViewById(R.id.guessEditText);

        // Resetovanje polja i početak nove igre
        if (!koraciList.isEmpty() && currentStep < koraciList.size()) {
            currentRound = koraciList.get(currentStep);
            // Ako postoji aktivni tajmer, prekini ga pre nego što ažurira korak i pokrenene novi tajmer
            if (timer != null) {
                timer.cancel();
            }
            // Dodaj poziv startTimer() ovde nakon što se završi dijalog
            updateStep();  // Prikazivanje novog koraka
        } else {
            endGame();
        }

        // Ostatak za resetovanje i početak nove igre
        currentStep = 1;
        score = 0;
        answerEditText.setText("");

        startNextGame();
    }

    private void endGame() {

        timerView.setText("");
        answerButton.setEnabled(false);
    }

    private boolean jeTacanOdgovor() {
        EditText answerEditText = findViewById(R.id.guessEditText);
        String unetiOdgovor = answerEditText.getText().toString().trim();

        // Provera da li uneti odgovor odgovara traženom pojmu
        return unetiOdgovor.equalsIgnoreCase(currentRound.getResenje());
    }

    private void startNextGame(){
        Intent intent = new Intent(this, Game6Activity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Izlaz iz igre");
        builder.setMessage("Da li ste sigurni da želite da izađete iz igre? Svi dosadašnji bodovi će biti izgubljeni.");

        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // Postavljanje da dijalog nije otkaziv (tj. da korisnik ne može kliknuti izvan dijaloga ili pritisnuti Back dugme)
        builder.setCancelable(false);

        // Kreiranje dijaloga
        AlertDialog exitDialog = builder.create();

        // Prikazivanje dijaloga
        exitDialog.show();
    }
}