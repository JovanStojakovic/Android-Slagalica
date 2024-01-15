package com.example.slagalicaprojekat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.slagalicaprojekat.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = findViewById(R.id.play);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Game3Activity.class));
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exsample_menu, menu);
        return true;


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Item2 ) {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);

            return true;

        }
        if (id == R.id.Item3 ) {
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

}


