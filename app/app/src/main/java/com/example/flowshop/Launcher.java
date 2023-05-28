package com.example.flowshop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.flowshop.client.RestClient;
import com.example.flowshop.screens.Login;
import com.example.flowshop.screens.Register;

public class Launcher extends AppCompatActivity {

    private Button registerButton, logerButton;
    private ImageView imageViewLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String sessionToken = preferences.getString("tokenSession", null);

        imageViewLogo = findViewById(R.id.logo);
        logerButton = findViewById(R.id.logerButton);
        registerButton = findViewById(R.id.registerButton);

        //Agrega la animación de entrada
        Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageViewLogo.setAnimation(fade_in);
        logerButton.setAnimation(fade_in);
        registerButton.setAnimation(fade_in);

        //Desactiva los botones durante la transición
        logerButton.setEnabled(false);
        registerButton.setEnabled(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Habilita los botones después de 2.5 segundos (después de la animación)
                logerButton.setEnabled(true);
                registerButton.setEnabled(true);
            }
        }, 2500);

        //Llamada al botón "INICIAR SESIÓN"
        logerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambio a la pantalla "Login"
                startActivity(new Intent(Launcher.this, Login.class));
            }
        });

        //Llamada al botón "REGÍSTRARSE"
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambio a la pantalla "Register"
                startActivity(new Intent(Launcher.this, Register.class));

            }
        });

        /*if (sessionToken == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            RestClient restClient = RestClient.getInstance(this);
            restClient.isLogged(sessionToken);
        }*/


    }
}