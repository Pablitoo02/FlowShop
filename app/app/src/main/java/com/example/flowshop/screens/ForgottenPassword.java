package com.example.flowshop.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowshop.R;
import com.example.flowshop.client.RestClient;
import com.example.flowshop.utils.ValidateEmail;

public class ForgottenPassword extends AppCompatActivity {
    private EditText email;
    private Button send;
    private Context context = this;
    private RestClient restClient = RestClient.getInstance(context);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        email = findViewById(R.id.emailRemind);

        send = findViewById(R.id.SendButton);
        send.setOnClickListener(remindListener);
    }

    //Método para mandar el email con el token de contraseña y cambiar a la pantalla "ReestablishPassword",
    //si el email está cubierto y de manera válida
    private View.OnClickListener remindListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!ValidateEmail.validateEmail(email.getText().toString())) {
                email.setError("Email no válido");
            } else if (email.getText().length() == 0) {
                email.setError("Campo obligatorio");
            } else {
                restClient.ForgottenPassword(email, context);
            }
        }
    };
}
