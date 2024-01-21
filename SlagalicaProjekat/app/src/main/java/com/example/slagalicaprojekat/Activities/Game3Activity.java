package com.example.slagalicaprojekat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalicaprojekat.Model.KoZnaZna;
import com.example.slagalicaprojekat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Game3Activity extends AppCompatActivity {

    private TextView questionTextView, timerTextView;
    private Button answer1Button, answer2Button, answer3Button, answer4Button;
    private int currentQuestionIndex = 0;
    private int playerScore = 0;
    private CountDownTimer timer;

    private DatabaseReference databaseReference;
    private List<KoZnaZna> questionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("KoZnaZna");

        questionTextView = findViewById(R.id.questionTextView);
        answer1Button = findViewById(R.id.answer1Button);
        answer2Button = findViewById(R.id.answer2Button);
        answer3Button = findViewById(R.id.answer3Button);
        answer4Button = findViewById(R.id.answer4Button);

        timerTextView = findViewById(R.id.timerTextView);

        timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Vreme: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                // Poziva se kada istekne vreme za odgovaranje
                nextQuestion();
            }
        };

        fetchQuestionsFromFirebase();

    }

    private void fetchQuestionsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Čitanje podataka iz baze
                questionsList.clear(); // Očisti prethodne podatke ako postoje

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    KoZnaZna question = snapshot.getValue(KoZnaZna.class);
                    if (question != null) {
                        questionsList.add(question);
                        Log.d("FirebaseData", "Učitano pitanje: " + question.getPitanje());
                    }
                }

                // Dodajte debug ispis
                Log.d("FirebaseData", "Učitana pitanja: " + questionsList.size());

                // Nakon što su pitanja preuzeta, prikaži prvo pitanje ako lista nije prazna
                if (!questionsList.isEmpty()) {
                    showQuestion();
                } else {
                    // Ako je lista pitanja prazna, završi igru
                    endGame();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // U slučaju greške pri dohvatanju podataka
                Toast.makeText(Game3Activity.this, "Greška u dohvatu podataka", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onAnswerButtonClick(View view) {
        String selectedAnswer = ((Button) view).getText().toString();
        checkAnswer(selectedAnswer);
    }

    private void checkAnswer(String selectedAnswer) {
        timer.cancel(); // Zaustavi tajmer jer je korisnik odgovorio

        // Provera da li lista ima barem jedan element
        if (!questionsList.isEmpty() && currentQuestionIndex < questionsList.size()) {
            KoZnaZna currentQuestion = questionsList.get(currentQuestionIndex);
            String correctAnswer = currentQuestion.getResenje();

            // Proveri odgovor
            if (correctAnswer.equals(selectedAnswer)) {
                playerScore += 10;
            } else {
                // Ako je odgovor netacan, oduzmite bodove (npr. -5 bodova)
                playerScore -= 5;
            }

            // Ukloni odgovoreno pitanje iz liste
            questionsList.remove(currentQuestion);

            nextQuestion();
        } else {

            endGame();
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            showQuestion();
        } else {
            endGame();
        }
    }


    private void showQuestion() {
        if (!questionsList.isEmpty() && currentQuestionIndex < questionsList.size()) {
            // Uzmi pitanje na osnovu trenutnog indeksa
            KoZnaZna currentQuestion = questionsList.get(currentQuestionIndex);

            // Postavi pitanje i odgovore na ekran
            questionTextView.setText(currentQuestion.getPitanje());
            answer1Button.setText(currentQuestion.getOpcija1());
            answer2Button.setText(currentQuestion.getOpcija2());
            answer3Button.setText(currentQuestion.getOpcija3());
            answer4Button.setText(currentQuestion.getResenje());

            // Pokreni tajmer za pitanje
            timer.start();
        } else {
            // Ako je lista pitanja prazna, završi igru
            endGame();
        }
    }

    private void endGame() {
        Log.d("GameEnd", "Prikaz dijaloga. Osvojili ste " + playerScore + " bodova!");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kraj igre");

        builder.setMessage("Osvojili ste " + playerScore + " bodova u ovoj igri. " +
                "Ukupno osvojeni bodovi do sada: " + playerScore + " bodova!");

        builder.setPositiveButton("Sledeća igra", (dialog, which) -> {
            Intent intent = new Intent(Game3Activity.this, Game4Activity.class);
            intent.putExtra("igra1", playerScore);
            intent.putExtra("ukupno-osvojeni-bodovi", playerScore); // Accumulate the scores
            startActivity(intent);
            finish();
        });

        builder.setCancelable(false);
        builder.show();
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