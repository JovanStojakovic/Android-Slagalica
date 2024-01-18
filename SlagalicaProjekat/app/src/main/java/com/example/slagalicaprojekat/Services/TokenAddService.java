package com.example.slagalicaprojekat.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TokenAddService extends IntentService {

    public TokenAddService() {
        super("TokenAddService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Izvršite logiku dodavanja tokena ovde
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentTokens = sharedPreferences.getInt("tokeni", 0);
        int newTokens = currentTokens + 5;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tokeni", newTokens);
        editor.apply();
    }

}
