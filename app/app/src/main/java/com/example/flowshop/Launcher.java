package com.example.flowshop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.flowshop.client.RestClient;
import com.example.flowshop.screens.Login;

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String sessionToken = preferences.getString("tokenSession", null);

        if (sessionToken == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        } else {
            RestClient restClient = RestClient.getInstance(this);
            restClient.isLogged(sessionToken);
        }

    }
}