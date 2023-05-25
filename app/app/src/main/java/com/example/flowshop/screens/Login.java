package com.example.flowshop.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowshop.Launcher;
import com.example.flowshop.R;
import com.example.flowshop.client.RestClient;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.flowshop.utils.ValidateEmail;

public class Login extends AppCompatActivity {

    private Button loginButton, registerButton, remindButton;
    private EditText email, password;
    private TextView showpassword;
    private ImageView imageViewLogo;
    private RequestQueue queue;
    private Context context = this;
    private RestClient restClient = RestClient.getInstance(context);
    private boolean canExitApp = false;
    private boolean hide = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        showpassword = findViewById(R.id.mostrar);
        showpassword.setOnClickListener(showListener);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(loginListener);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(registerListener);

        remindButton = findViewById(R.id.remindButton);
        remindButton.setOnClickListener(remindListener);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);

        imageViewLogo = findViewById(R.id.logoImageView);

        queue = Volley.newRequestQueue(this);

    }

    // Método volver a la pantalla de Launcher
    @Override
    public void onBackPressed(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, Launcher.class));
            }
        }, 500);
    }

    private View.OnClickListener showListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (password.getText().toString().isEmpty()) {
                password.setError("Falta Contraseña");
            } else {
                if (hide) {
                    hide = false;
                    password.setTransformationMethod(null);
                } else {
                    hide = true;
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        }
    };

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (email.getText().length() == 0) {
                email.setError("Campo obligatorio");
            } else if(!ValidateEmail.validateEmail(email.getText().toString())){
                email.setError("Email no valido");
            } else if( password.getText().length() == 0) {
                password.setError("Campo obligatorio");
            } else {
                restClient.login(email, password, context);
            }


        }
    };

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, Register.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener remindListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Login.this, ForgottenPassword.class);
            startActivity(intent);
        }
    };
}