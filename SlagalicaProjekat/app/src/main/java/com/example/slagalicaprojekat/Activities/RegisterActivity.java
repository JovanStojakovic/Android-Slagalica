package com.example.slagalicaprojekat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slagalicaprojekat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://slagalica-3c783-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final TextView loginNow=findViewById(R.id.alredyHaveAccount);
        final EditText email = findViewById((R.id.inputEmail));
        final EditText username = findViewById((R.id.inputUsername));
        final EditText password = findViewById((R.id.inputPassword));
        final EditText ponovoPassword = findViewById((R.id.ponovoPassword));
        final Button register = findViewById((R.id.btnRegister));

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prikaz polja u koje cemo da unosimo vrednosti
                final String usernameTxt = username.getText().toString();
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String ponovoPasswordTxt = ponovoPassword.getText().toString();

                if (usernameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if(!passwordTxt.equals(ponovoPasswordTxt)) {
                    Toast.makeText(RegisterActivity.this, "Password are not matching", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(usernameTxt)){
                                Toast.makeText(RegisterActivity.this, "Username is alredy registered", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                //dodaj podatke u bazu
                                databaseReference.child("users").child(usernameTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(usernameTxt).child("username").setValue(usernameTxt);
                                databaseReference.child("users").child(usernameTxt).child("password").setValue(passwordTxt);
                                databaseReference.child("users").child(usernameTxt).child("tokeni").setValue(5);
                                databaseReference.child("users").child(usernameTxt).child("zvezde").setValue(0);

                                Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                finish();
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


}