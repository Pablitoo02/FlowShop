package com.example.flowshop.screens;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowshop.R;
import com.example.flowshop.client.RestClient;

public class ReestablishPassword extends AppCompatActivity {
    private TextView passwordToken, newPassword, newPassword2;
    private Button submitButton;
    private TextView showPassword;
    private boolean hide = true;
    private RestClient restClient = RestClient.getInstance(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reestablish_password);

        passwordToken = findViewById(R.id.tokenPassword);

        newPassword = findViewById(R.id.newPassword);

        newPassword2 = findViewById(R.id.newPassword2);

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(submitListener);

        showPassword = findViewById(R.id.showPassword);
        showPassword.setOnClickListener(showPasswordListener);
    }

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (passwordToken.getText().length() == 0 )
                passwordToken.setError("Campo obligatorio");
            else if (newPassword.getText().length() == 0)
                newPassword.setError("Campo obligatorio");
            else if (newPassword2.getText().length() == 0)
                newPassword2.setError("Campo obligatorio");
            else if (!newPassword.getText().toString().equals(newPassword2.getText().toString()))
                newPassword.setError("Las contraseñas no coinciden");
            else {
                restClient.reestablishPassword(passwordToken.getText().toString(), newPassword.getText().toString());
            }
        }
    };

    View.OnClickListener showPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (newPassword.getText().toString().isEmpty() && newPassword2.getText().toString().isEmpty()) {
                newPassword.setError("Falta Contraseña");
                newPassword2.setError("Falta Contraseña");
            } else {
                if (hide) {
                    hide = false;
                    newPassword.setTransformationMethod(null);
                    newPassword2.setTransformationMethod(null);
                } else {
                    hide = true;
                    newPassword.setTransformationMethod(new PasswordTransformationMethod());
                    newPassword2.setTransformationMethod(new PasswordTransformationMethod());

                }
            }
        }
    };
}
