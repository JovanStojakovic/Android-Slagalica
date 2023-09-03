package com.example.slagalicaprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    private DatabaseReference databaseReference;
    private List<Button> buttonList;
    private int currentButtonIndex;
    private KorakPoKorak selectedObject;

    private CountDownTimer timer;
    private TextView timerTextView;

    private TextView pointsTextView;
    private final long TIMER_DURATION = 11000; // 10 seconds

    private int totalPoints;
    private boolean isPointsDialogShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4);
        resetPoints();

        Button btn = findViewById(R.id.btnConfirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Game4Activity.this,MyProfileActivity.class));
            }
        });

        FirebaseApp.initializeApp(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        buttonList = new ArrayList<>();
        buttonList.add(findViewById(R.id.btnTerm1));
        buttonList.add(findViewById(R.id.btnTerm2));
        buttonList.add(findViewById(R.id.btnTerm3));
        buttonList.add(findViewById(R.id.btnTerm4));
        buttonList.add(findViewById(R.id.btnTerm5));
        buttonList.add(findViewById(R.id.btnTerm6));
        buttonList.add(findViewById(R.id.btnTerm7));

        timerTextView = findViewById(R.id.tajmer);
        pointsTextView = findViewById(R.id.tvScore);
        pointsTextView.setText("Bodovi: " + String.valueOf(getTotalPoints()));

        readDataFromFirebase();
    }

//Metoda readDataFromFirebase() čita podatke iz Firebase Realtime Database, konkretno čita podatke sa putanje "KorakPoKorak".
    private void readDataFromFirebase() {

        DatabaseReference objectsRef = databaseReference.child("KorakPoKorak");


        objectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<KorakPoKorak> objectList = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        KorakPoKorak object = snapshot.getValue(KorakPoKorak.class);
                        if (object != null) {
                            objectList.add(object);
                        }
                    }


                    Random random = new Random();
                    int randomIndex = random.nextInt(objectList.size());
                    selectedObject = objectList.get(randomIndex);

                    startButtonUpdates();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving data from Firebase: " + databaseError.getMessage());
            }
        });
    }
    private void startButtonUpdates() {
        currentButtonIndex = 0;
        updateButton();
    }



    //Metoda updateButton() ažurira trenutni dugme u igri. Ona postavlja tekst na trenutnom
    // dugmetu na osnovu odabranog objekta iz baze podataka. Takođe, postavlja osluškivač događaja na polje za unos teksta,
    // koji proverava da li je korisnik unio tačno rešenje. Ako je korisnik uneo tačno rešenje, zaustavlja se ažuriranje dugmadi,
    // računa se broj osvojenih bodova, i prikazuje se dijalog sa bodovima. U suprotnom, prikazuje se dijalog
    // za pogrešno rešenje i priprema se sledeće dugme za ažuriranje.
    private void updateButton() {
        if (currentButtonIndex < buttonList.size()) {
            Button currentButton = buttonList.get(currentButtonIndex);

            String propertyName = "korak" + (currentButtonIndex + 1);
            String propertyValue = getProperty(selectedObject, propertyName);

            currentButton.setText(propertyValue);

            currentButtonIndex++;

            EditText solutionEditText = findViewById(R.id.etSolution);



            solutionEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = v.getText().toString();
                    if (userInput.equalsIgnoreCase(selectedObject.getResenje())) {
                        stopButtonUpdates();
                        int currentButtonIndex = getCurrentButtonIndex();
                        int points = 22 - currentButtonIndex * 2;
                        showPointsDialog(points);
                    }else {
                        showWrongSolutionAlert();
                        renderNextButton();
                    }
                    return true;
                }
                return false;
            });


            if (currentButtonIndex >= buttonList.size()) {

                solutionEditText.setText(selectedObject.getResenje());
            } else {

                startTimer();
            }
        }
    }

    //Metoda showWrongSolutionAlert() prikazuje dijalog sa porukom "Pogresno resenje" kada korisnik unese netačno rešenje.
    private void showWrongSolutionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pogresno resenje");
        builder.setMessage("Pogresno resenje.");
        builder.setPositiveButton("OK", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Metoda renderNextButton() ažurira sledeće dugme u nizu dugmadi. Postavlja tekst na sledećem dugmetu
    // na osnovu odabranog objekta iz baze podataka, povećava indeks trenutnog dugmeta, i pokreće tajmer.
    private void renderNextButton() {
        if (currentButtonIndex < buttonList.size()) {
            Button currentButton = buttonList.get(currentButtonIndex);

            String propertyName = "step" + (currentButtonIndex + 1);
            String propertyValue = getProperty(selectedObject, propertyName);

            currentButton.setText(propertyValue);

            currentButtonIndex++;

            startTimer();
        }
    }
    private void stopButtonUpdates() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private int getCurrentButtonIndex() {
        return currentButtonIndex;
    }

    //Metoda startTimer() pokreće tajmer koji odbrojava određeno vreme.
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(TIMER_DURATION, 1000) {

            //onTick() - koja se izvršava svake sekunde i ažurira prikaz preostalog vremena na timerTextView
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                timerTextView.setText(String.valueOf(secondsRemaining));
            }
            //onFinish() - koja se izvršava kada tajmer dostigne nula sekundi.

            @Override
            public void onFinish() {
                updateButton();
            }
        };

        timer.start();
    }

    private String getProperty(Object object, String propertyName) {
        try {
            Field field = object.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            Object value = field.get(object);
            if (value != null) {
                return value.toString();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
//Metoda showPointsDialog() prikazuje dijalog sa osvojenim bodovima. Provjerava da li je dijalog već prikazan,
// a zatim čuva osvojene bodove i prikazuje dijalog sa porukom koja sadrži broj bodova. Kada korisnik zatvori dijalog,
// metoda označava da je dijalog zatvoren.
    private void showPointsDialog(int points) {

        savePoints(points);
        int totalPoints = getTotalPoints();
        //endGame();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bodovi");
        builder.setMessage("Osvojili ste " + points + " bodova!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            isPointsDialogShown = false;

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Metoda savePoints() čuva osvojene bodove korisnika.
    private void savePoints(int points) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int totalPoints = preferences.getInt("totalPoints", 0);
        totalPoints += points;
        pointsTextView.setText("Bodovi: " + String.valueOf(totalPoints)); // Update pointsTextView with the total points
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("totalPoints", totalPoints);
        editor.apply();
    }
//Metoda getTotalPoints() vraća ukupan broj bodova koji je korisnik osvojio.
    private int getTotalPoints() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getInt("totalPoints", 0);
    }
// Ovo se koristi za resetovanje bodova na početno stanje. Ovo se koristi za resetovanje bodova na početno stanje.
    private void resetPoints() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("totalPoints", 0);
        editor.apply();
    }
    /*private void endGame() {
        String points = String.valueOf(getTotalPoints());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Points");
        builder.setMessage("Kraj igre, ukupno imate " + points + " bodova!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle the OK button click if needed
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

}