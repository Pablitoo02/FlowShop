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

    private View.OnClickListener remindListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!ValidateEmail.validateEmail(email.getText().toString())) {
                email.setError("Email no válido");
            } else if (email.getText().length() == 0) {
                email.setError("Campo obligatorio");
            } else {
                restClient.ForgottenPassword(email, context);

                HideKeyboard();
            }
        }
    };

    private void HideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
