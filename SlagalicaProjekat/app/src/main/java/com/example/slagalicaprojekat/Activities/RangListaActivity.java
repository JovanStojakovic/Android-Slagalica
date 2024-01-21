package com.example.slagalicaprojekat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.slagalicaprojekat.Adapter.CustomArrayAdapter;
import com.example.slagalicaprojekat.Model.KorakPoKorak;
import com.example.slagalicaprojekat.Model.Korisnik;
import com.example.slagalicaprojekat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RangListaActivity extends AppCompatActivity {

    private DatabaseReference usersRef;
    private ListView rankListView;
    private List<Korisnik> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rang_lista);

        rankListView = findViewById(R.id.rankListView);
        userList = new ArrayList<>();

        // Inicijalizujemo Firebase bazu podataka
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Dohvatimo sve korisnike iz baze podataka
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Prilagodili smo kod da dohvata vrednosti direktno iz DataSnapshot-a
                    String username = userSnapshot.child("username").getValue(String.class);
                    int zvezde = userSnapshot.child("zvezde").getValue(Integer.class);
                    int odigranePartije = userSnapshot.child("odigranePartije").getValue(Integer.class);
                    String email = userSnapshot.child("email").getValue(String.class);

                    Korisnik korisnik = new Korisnik(username, zvezde, odigranePartije, email);
                    userList.add(korisnik);
                }

                // Sortiramo korisnike prema broju zvezdica (u opadajućem redosledu)
                Collections.sort(userList, new Comparator<Korisnik>() {
                    @Override
                    public int compare(Korisnik user1, Korisnik user2) {
                        return Integer.compare(user2.getZvezde(), user1.getZvezde());
                    }
                });

                // Prikazujemo rang listu korisnika
                showRankList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRankList() {
        CustomArrayAdapter adapter = new CustomArrayAdapter(this, userList);
        rankListView.setAdapter(adapter);
    }
}