package com.example.slagalicaprojekat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class LogUserActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_user);

        Button btn1 = findViewById(R.id.profile);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogUserActivity.this,MyProfileActivity.class));
            }
        });
        Button btn2 = findViewById(R.id.play);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogUserActivity.this,Game5Activity.class));
            }
        });
        Button btn3 = findViewById(R.id.playFriend);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogUserActivity.this,Game5Activity.class));
            }
        });



    }
}