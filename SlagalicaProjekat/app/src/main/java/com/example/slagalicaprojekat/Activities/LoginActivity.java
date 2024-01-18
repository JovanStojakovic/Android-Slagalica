package com.example.slagalicaprojekat.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slagalicaprojekat.R;
import com.example.slagalicaprojekat.Services.TokenAddService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://slagalica-3c783-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView btn=findViewById(R.id.textViewSingUp);
        final EditText username = findViewById((R.id.inputUsername));
        final EditText password = findViewById((R.id.inputPassword));
        final Button loginBtn = findViewById(R.id.btnlogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String usernameTxt = username.getText().toString();
                final String passwordTxt = password.getText().toString();

                if (usernameTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your username or paassword", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(usernameTxt)){
                                final String getPassword = snapshot.child(usernameTxt).child("password").getValue(String.class);
                                if(getPassword.equals(passwordTxt)){
                                    final String email = snapshot.child(usernameTxt).child("email").getValue(String.class);
                                    final Long zvezdeLong = snapshot.child(usernameTxt).child("zvezde").getValue(Long.class);
                                    final Long tokeniLong = snapshot.child(usernameTxt).child("tokeni").getValue(Long.class);
                                    final Long odigranePartijeLong = snapshot.child(usernameTxt).child("odigranePartije").getValue(Long.class);
                                    int zvezde = (zvezdeLong != null) ? zvezdeLong.intValue() : 0;
                                    int tokeni = (tokeniLong != null) ? tokeniLong.intValue() : 0;
                                    int odigranePartije = (odigranePartijeLong != null) ? odigranePartijeLong.intValue() : 0;

                                    //kad je uspesno logovanje sacuvaj te podatke o prijavljenom korisniku
                                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", usernameTxt); //sacuvaj username
                                    editor.putString("email", email); //sacuvaj email
                                    editor.putInt("zvezde", zvezde);
                                    editor.putInt("tokeni", tokeni);
                                    editor.putInt("odigranePartije", odigranePartije);
                                    editor.apply();

                                    scheduleTokenAdding();
                                    updateTokensInDatabase(usernameTxt);

                                    Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, LogUserActivity.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();

                                }
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Wrong username", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


    }

    private void scheduleTokenAdding() {
        Intent intent = new Intent(this, TokenAddService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Postavite AlarmManager da pokrene uslugu svakih 10 min
        long interval = 10 * 60 * 1000; // 10 min u milisekundama
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
    }

    private void updateTokensInDatabase(final String username) {
        // Učitaj trenutni broj tokena iz baze
        databaseReference.child("users").child(username).child("tokeni").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long currentTokens = snapshot.getValue(Long.class);

                    // Dodaj 5 tokena
                    long newTokens = currentTokens + 5;

                    // Ažuriraj broj tokena u bazi
                    databaseReference.child("users").child(username).child("tokeni").setValue(newTokens);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handlujte greške
            }
        });
    }

}
