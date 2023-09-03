package com.example.slagalicaprojekat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game5Activity extends AppCompatActivity {

    private Button btnNextGame;
    private DatabaseReference databaseReference;
    private Asocijacije selectedObject;
    private EditText solutionEditTextA, solutionEditTextB, solutionEditTextC, solutionEditTextD, finalResult;
    private Button[] buttons;

    private int totalPoints;
    private TextView pointsTextView;

    private int points;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game5);
        resetPoints();
        btnNextGame = (Button) findViewById(R.id.btn_nextGame);
        btnNextGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Game5Activity.this, Game4Activity.class));
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        readDataFromFirebase();

        buttons = new Button[16];
        buttons[0] = findViewById(R.id.button_submit);
        buttons[1] = findViewById(R.id.button_submit2);
        buttons[2] = findViewById(R.id.button_submit3);
        buttons[3] = findViewById(R.id.button_submit4);
        buttons[4] = findViewById(R.id.button_submit5);
        buttons[5] = findViewById(R.id.button_submit6);
        buttons[6] = findViewById(R.id.button_submit7);
        buttons[7] = findViewById(R.id.button_submit8);
        buttons[8] = findViewById(R.id.button_submit9);
        buttons[9] = findViewById(R.id.button_submit10);
        buttons[10] = findViewById(R.id.button_submit11);
        buttons[11] = findViewById(R.id.button_submit12);
        buttons[12] = findViewById(R.id.button_submit13);
        buttons[13] = findViewById(R.id.button_submit14);
        buttons[14] = findViewById(R.id.button_submit15);
        buttons[15] = findViewById(R.id.button_submit16);

        solutionEditTextA = findViewById(R.id.solutionEditTextA);
        solutionEditTextB = findViewById(R.id.solutionEditTextB);
        solutionEditTextC = findViewById(R.id.solutionEditTextC);
        solutionEditTextD = findViewById(R.id.solutionEditTextD);
        finalResult = findViewById(R.id.finalResult);
        pointsTextView = findViewById(R.id.pointsTextView);

        for (int i = 0; i < buttons.length; i++) {
            final int index = i; // Create a final variable for the click listener

            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String buttonText = getButtonTextForIndex(index);
                    buttons[index].setText(buttonText);
                }
            });
        }


    }


    private String getButtonTextForIndex(int index) {
        switch (index) {
            case 0:
                return selectedObject.getA1();
            case 1:
                return selectedObject.getA2();
            case 2:
                return selectedObject.getA3();
            case 3:
                return selectedObject.getA4();
            case 4:
                return selectedObject.getB1();
            case 5:
                return selectedObject.getB2();
            case 6:
                return selectedObject.getB3();
            case 7:
                return selectedObject.getB4();
            case 8:
                return selectedObject.getC1();
            case 9:
                return selectedObject.getC2();
            case 10:
                return selectedObject.getC3();
            case 11:
                return selectedObject.getC4();
            case 12:
                return selectedObject.getD1();
            case 13:
                return selectedObject.getD2();
            case 14:
                return selectedObject.getD3();
            case 15:
                return selectedObject.getD4();
            // Add cases for the rest of the button indices
            default:
                return "";
        }
    }



    private void readDataFromFirebase() {
        DatabaseReference objectsRef = databaseReference.child("Asocijacije");

        objectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Asocijacije> objectList = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Asocijacije object = snapshot.getValue(Asocijacije.class);
                        if (object != null) {
                            objectList.add(object);
                        }
                    }

                    Random random = new Random();
                    int randomIndex = random.nextInt(objectList.size());
                    selectedObject = objectList.get(randomIndex);

                    solutionEditTextA.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String userInput = s.toString().trim();
                            if (userInput.equalsIgnoreCase(selectedObject.getResenjeKoloneA())) {
                                solutionEditTextA.removeTextChangedListener(this);
                                solutionEditTextA.setText(selectedObject.getResenjeKoloneA());
                                solutionEditTextA.setEnabled(false);
                                for (int i = 0; i < 4; i++) {
                                    Button button = buttons[i];
                                    String buttonText = button.getText().toString().trim();
                                    String expectedText = getButtonTextForIndex(i);
                                    if (!buttonText.equalsIgnoreCase(expectedText)) {
                                        points += 1;
                                    }
                                }
                                buttons[0].setText(getButtonTextForIndex(0));
                                buttons[1].setText(getButtonTextForIndex(1));
                                buttons[2].setText(getButtonTextForIndex(2));
                                buttons[3].setText(getButtonTextForIndex(3));
                                points += 2;

                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    solutionEditTextB.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String userInput = s.toString().trim();
                            if (userInput.equalsIgnoreCase(selectedObject.getResenjeKoloneB())) {
                                solutionEditTextB.removeTextChangedListener(this);
                                solutionEditTextB.setText(selectedObject.getResenjeKoloneB());
                                solutionEditTextB.setEnabled(false);
                                for (int i = 4; i < 8; i++) {
                                    Button button = buttons[i];
                                    String buttonText = button.getText().toString().trim();
                                    String expectedText = getButtonTextForIndex(i);
                                    if (!buttonText.equalsIgnoreCase(expectedText)) {
                                        points += 1;
                                    }
                                }
                                buttons[4].setText(getButtonTextForIndex(4));
                                buttons[5].setText(getButtonTextForIndex(5));
                                buttons[6].setText(getButtonTextForIndex(6));
                                buttons[7].setText(getButtonTextForIndex(7));
                                points += 2;
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    solutionEditTextC.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String userInput = s.toString().trim();
                            if (userInput.equalsIgnoreCase(selectedObject.getResenjeKoloneC())) {
                                solutionEditTextC.removeTextChangedListener(this);
                                solutionEditTextC.setText(selectedObject.getResenjeKoloneC());
                                solutionEditTextC.setEnabled(false);
                                for (int i = 8; i < 12; i++) {
                                    Button button = buttons[i];
                                    String buttonText = button.getText().toString().trim();
                                    String expectedText = getButtonTextForIndex(i);
                                    if (!buttonText.equalsIgnoreCase(expectedText)) {
                                        points += 1;
                                    }
                                }
                                buttons[8].setText(getButtonTextForIndex(8));
                                buttons[9].setText(getButtonTextForIndex(9));
                                buttons[10].setText(getButtonTextForIndex(10));
                                buttons[11].setText(getButtonTextForIndex(11));
                                points += 2;
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    solutionEditTextD.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String userInput = s.toString().trim();
                            if (userInput.equalsIgnoreCase(selectedObject.getResenjeKoloneD())) {
                                solutionEditTextD.removeTextChangedListener(this);
                                solutionEditTextD.setText(selectedObject.getResenjeKoloneD());
                                solutionEditTextD.setEnabled(false);
                                for (int i = 12; i < 16; i++) {
                                    Button button = buttons[i];
                                    String buttonText = button.getText().toString().trim();
                                    String expectedText = getButtonTextForIndex(i);
                                    if (!buttonText.equalsIgnoreCase(expectedText)) {
                                        points += 1;
                                    }
                                }
                                buttons[12].setText(getButtonTextForIndex(12));
                                buttons[13].setText(getButtonTextForIndex(13));
                                buttons[14].setText(getButtonTextForIndex(14));
                                buttons[15].setText(getButtonTextForIndex(15));
                                points += 2;

                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    finalResult.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String userInput = s.toString().trim();
                            if (userInput.equalsIgnoreCase(selectedObject.getKonacnoResenje())) {
                                finalResult.removeTextChangedListener(this);
                                finalResult.setText(selectedObject.getKonacnoResenje());
                                finalResult.setEnabled(false);

                                if (solutionEditTextA.isEnabled()) {
                                    points += 2;
                                }
                                // Check if solutionEditTextB is disabled and add points accordingly
                                if (solutionEditTextB.isEnabled()) {
                                    points += 2;
                                }
                                // Check if solutionEditTextC is disabled and add points accordingly
                                if (solutionEditTextC.isEnabled()) {
                                    points += 2;
                                }
                                // Check if solutionEditTextD is disabled and add points accordingly
                                if (solutionEditTextD.isEnabled()) {
                                    points += 2;
                                }


                                for (int i = 0; i < buttons.length; i++) {
                                    Button button = buttons[i];
                                    String buttonText = button.getText().toString().trim();
                                    String expectedText = getButtonTextForIndex(i);
                                    if (!buttonText.equalsIgnoreCase(expectedText)) {
                                        points += 1;
                                    }
                                }
                                points += 7;
                                showPointsDialog(points);


                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                }
            }





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Error retrieving data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void showPointsDialog(int points) {
        savePoints(points);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Points");
        builder.setMessage("Osvojili ste " + points + " bodova!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle the OK button click if needed
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void savePoints(int points) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int totalPoints = preferences.getInt("totalPoints", 0);
        totalPoints += points;
        pointsTextView.setText("Bodovi: " + String.valueOf(totalPoints)); // Update pointsTextView with the total points
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("totalPoints", totalPoints);
        editor.apply();
    }


    private int getTotalPoints() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getInt("totalPoints", 0);
    }

    private void resetPoints() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("totalPoints", 0);
        editor.apply();
    }
}
