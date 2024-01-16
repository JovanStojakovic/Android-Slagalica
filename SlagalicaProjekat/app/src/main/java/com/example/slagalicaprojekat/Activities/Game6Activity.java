package com.example.slagalicaprojekat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


import com.example.slagalicaprojekat.Model.MojBroj;
import com.example.slagalicaprojekat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Game6Activity extends AppCompatActivity {

    private MojBroj currentBroj;
    private DatabaseReference databaseReference;
    private int stopButtonClickCount = 0;
    private int score = 0;
    private int bodoviZaOdgovor = 0;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game6);

        stopButton = findViewById(R.id.stopButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("MojBroj");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        // Očekujemo da su podaci u obliku liste (ArrayList)
                        GenericTypeIndicator<ArrayList<HashMap<String, Object>>> typeIndicator =
                                new GenericTypeIndicator<ArrayList<HashMap<String, Object>>>() {};
                        ArrayList<HashMap<String, Object>> dataList = dataSnapshot.getValue(typeIndicator);

                        if (dataList != null && dataList.size() > 0) {
                            // Uzimamo prvi objekat iz liste
                            HashMap<String, Object> brojMap = dataList.get(0);

                            // Inicijalizacija broja iz podataka
                            currentBroj = initializeBroj(brojMap);

                        } else {
                            Log.e("MojBrojActivity", "onDataChange: DataList is null or empty");
                        }
                    } else {
                        Log.e("MojBrojActivity", "onDataChange: DataSnapshot is empty");
                    }
                } catch (Exception e) {
                    Log.e("MojBrojActivity", "onDataChange: Error", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obrada grešaka
            }
        });

        score = 0;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ukupno-osvojeni-bodovi")) {
            int ukupnoBodova = intent.getIntExtra("ukupno-osvojeni-bodovi", 0);
            score += ukupnoBodova;
        }

    }

    private MojBroj initializeBroj(HashMap<String, Object> brojMap) {
        int number1 = ((Long) brojMap.get("number1")).intValue();
        int number2 = ((Long) brojMap.get("number2")).intValue();
        int number3 = ((Long) brojMap.get("number3")).intValue();
        int number4 = ((Long) brojMap.get("number4")).intValue();
        int number5 = ((Long) brojMap.get("number5")).intValue();
        int number6 = ((Long) brojMap.get("number6")).intValue();
        int result = ((Long) brojMap.get("result")).intValue();

        return new MojBroj(number1, number2, number3, number4, number5, number6, result);
    }

    private void setNumberButtonsVisibility(int visibility, MojBroj broj) {
        Button[] numberButtons = {
                findViewById(R.id.numberButton1),
                findViewById(R.id.numberButton2),
                findViewById(R.id.numberButton3),
                findViewById(R.id.numberButton4),
                findViewById(R.id.numberButton5),
                findViewById(R.id.numberButton6)
        };

        int[] numbers = {
                broj.getNumber1(),
                broj.getNumber2(),
                broj.getNumber3(),
                broj.getNumber4(),
                broj.getNumber5(),
                broj.getNumber6()
        };

        for (int i = 0; i < numberButtons.length; i++) {
            numberButtons[i].setText(String.valueOf(numbers[i]));
            numberButtons[i].setVisibility(visibility);
        }
    }


    public void stopButtonClicked(View view) {
        Log.d("MojBrojActivity", "stopButtonClicked: Stop button clicked");

        if (currentBroj != null) {
            TextView targetNumberTextView = findViewById(R.id.targetNumberTextView);

            // Postavi ciljni broj u TextView
            targetNumberTextView.setText("Traženi broj: " + currentBroj.getResult());

            // Increment the stopButtonClickCount
            stopButtonClickCount++;

            // If the button is clicked for the second time, make the number buttons visible
            if (stopButtonClickCount == 2) {
                setNumberButtonsVisibility(View.VISIBLE, currentBroj);
                // Reset the click count for future use
                stopButtonClickCount = 0;
                stopButton.setVisibility(View.INVISIBLE);
            } else {
                // If it's the first click, set the buttons invisible
                setNumberButtonsVisibility(View.INVISIBLE, currentBroj);
            }

        } else {
            Log.d("MojBrojActivity", "stopButtonClicked: CurrentBroj is null");
            // Dodajte odgovarajuću logiku ili poruku ako currentBroj nije postavljen
        }
    }

    public void numberButtonClicked(View view) {
        Button clickedButton = (Button) view;
        String currentExpression = ((TextView) findViewById(R.id.expressionTextView)).getText().toString();
        currentExpression += clickedButton.getText().toString();
        ((TextView) findViewById(R.id.expressionTextView)).setText(currentExpression);
    }

    public void operationButtonClicked(View view) {
        Button clickedButton = (Button) view;
        String currentExpression = ((TextView) findViewById(R.id.expressionTextView)).getText().toString();
        currentExpression += clickedButton.getText().toString();
        ((TextView) findViewById(R.id.expressionTextView)).setText(currentExpression);
    }

    public void confirmButtonClicked(View view) {
        String currentExpression = ((TextView) findViewById(R.id.expressionTextView)).getText().toString();

        // Koristi Rhino za evaluaciju izraza
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1); // Isključuje optimizaciju za bolju kompatibilnost
        Scriptable scope = rhino.initStandardObjects();

        try {
            Object result = rhino.evaluateString(scope, currentExpression, "JavaScript", 1, null);

            // Provera rezultata
            int targetNumber = currentBroj.getResult();
            int calculatedResult = (int) Context.toNumber(result);

            if (calculatedResult == targetNumber) {
                // Logika ako je rezultat tačan
                bodoviZaOdgovor += 20; // Dodavanje 20 bodova za tačan odgovor
                showResultDialog(true);
            } else {
                // Logika ako rezultat nije tačan
                showResultDialog(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Logika ako dođe do greške prilikom evaluacije izraza
        } finally {
            Context.exit();
        }

        score += bodoviZaOdgovor;

        // Reset bodoviZaOdgovor for the next round
        bodoviZaOdgovor = 0;
    }

    private void showResultDialog(boolean isCorrect) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isCorrect ? "Tačno!" : "Netačno!");

        if (isCorrect) {
            builder.setMessage("Čestitamo! Osvojili ste " + bodoviZaOdgovor + " bodova u ovoj igri. " +
                    "Ukupno osvojenih bodova: " + (score + bodoviZaOdgovor) + " boda!");
            builder.setPositiveButton("Kraj", ((dialog, which) -> {
                Intent intent = new Intent(Game6Activity.this, KrajIgreActivity.class);
                intent.putExtra("ukupno-osvojeni-bodovi", score ); // Ovde dodajte bodove
                startActivity(intent);
                finish();
            }));
        } else {
            builder.setMessage("Nažalost, niste tačno odgovorili." + "\n" +
                    "Ukupno osvojenih bodova: " + score + " boda!");
            builder.setPositiveButton("Kraj", ((dialog, which) -> {
                Intent intent = new Intent(Game6Activity.this,KrajIgreActivity.class);
                intent.putExtra("ukupno-osvojeni-bodovi", score ); // Ovde dodajte bodove
                startActivity(intent);
                finish();
            }));
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteButtonClicked(View view) {
        ((TextView) findViewById(R.id.expressionTextView)).setText("");
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