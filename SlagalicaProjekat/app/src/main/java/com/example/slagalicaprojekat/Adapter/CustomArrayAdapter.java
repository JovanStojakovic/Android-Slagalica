package com.example.slagalicaprojekat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slagalicaprojekat.Activities.MyProfileActivity;
import com.example.slagalicaprojekat.Activities.UserProfileActivity;
import com.example.slagalicaprojekat.Model.Korisnik;
import com.example.slagalicaprojekat.R;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Korisnik> {

    private Context context;

    public CustomArrayAdapter(Context context, List<Korisnik> userList) {
        super(context, 0, userList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Korisnik korisnik = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView zvezdeTextView = convertView.findViewById(R.id.zvezdeTextView);
        TextView emailTextView = convertView.findViewById(R.id.emailTextViewHidden);
        TextView odigranePartijeTextView = convertView.findViewById(R.id.odigranePartijeTextViewHidden);


        usernameTextView.setText(korisnik.getUsername());
        zvezdeTextView.setText(String.valueOf(korisnik.getZvezde()));
        emailTextView.setText(korisnik.getEmail());
        odigranePartijeTextView.setText(String.valueOf(korisnik.getOdigranePartije()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfile(korisnik);
            }
        });

        return convertView;
    }

    private void openUserProfile(Korisnik korisnik) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("username", korisnik.getUsername());
        intent.putExtra("zvezde", korisnik.getZvezde());
        intent.putExtra("email", korisnik.getEmail());
        intent.putExtra("odigranePartije", korisnik.getOdigranePartije());
        context.startActivity(intent);
    }
}
