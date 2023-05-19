package com.example.flowshop.screens;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowshop.R;
import com.example.flowshop.client.RestClient;
import com.example.flowshop.utils.DatePickerFragment;
import com.example.flowshop.utils.ValidateEmail;

import java.util.Calendar;

public class Register extends AppCompatActivity {
    private EditText editTextEmail,editTextPassword,editTextPassword2,editTextName,editTextBirthDate,editTextSurnames;
    private Button registerButton;
    private Context context = this;
    private ImageView imagen;
    private TextView mostrarContraseña,iniciarsesion;
    private Boolean hide = true;
    private RestClient restClient = RestClient.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imagen = findViewById(R.id.imageView);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextSurnames = findViewById(R.id.editTextSurnames);
        editTextBirthDate = findViewById(R.id.editTextBirthDate);
        editTextPassword2 = findViewById(R.id.editTextTextPassword2);
        iniciarsesion = findViewById(R.id.iniciar_sesion);
        mostrarContraseña = findViewById(R.id.MostrarContraseña);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        year = year-18;
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        editTextBirthDate.setText(year + "-" + twoDigits(month + 1) + "-" + twoDigits(day));

        restClient.logo(imagen);

        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Login.class);
                startActivity(intent);
            }
        });

        mostrarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getText().toString().isEmpty() && editTextPassword2.getText().toString().isEmpty()) {
                    editTextPassword.setError("Falta Contraseña");
                    editTextPassword2.setError("Falta Contraseña");
                } else {
                    if (hide) {
                        hide = false;
                        editTextPassword.setTransformationMethod(null);
                        editTextPassword2.setTransformationMethod(null);
                    } else {
                        hide = true;
                        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                        editTextPassword2.setTransformationMethod(new PasswordTransformationMethod());
                    }
                }
            }
        });

        editTextBirthDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }

        });

        registerButton = findViewById(R.id.registerbutton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidateEmail.validateEmail(editTextEmail.getText().toString()))
                    editTextEmail.setError("Email no válido");

                if (!editTextPassword.getText().toString().equals(editTextPassword2.getText().toString())) {
                    editTextPassword.setError("Contraseñas diferentes");
                    editTextPassword2.setError("Contraseñas diferentes");
                }

                if (editTextPassword.length() == 0)
                    editTextPassword.setError("Falta Contraseña");

                if (editTextPassword2.length() == 0)
                    editTextPassword2.setError("Falta Contraseña");

                if (editTextName.length() == 0)
                    editTextName.setError("Falta Nombre");

                if (editTextSurnames.length() == 0)
                    editTextSurnames.setError("Falta Apellidos");

                if (editTextEmail.length() == 0)
                    editTextEmail.setError("Falta Email");

                if (editTextName.getError() == null && editTextPassword.getError() == null && editTextSurnames.getError() == null && editTextPassword2.getError() == null && editTextEmail.getError() == null ){
                    restClient.register(editTextName,editTextSurnames,editTextEmail,editTextPassword,editTextPassword2,editTextBirthDate);

                }
            }
        });
    }

    private void showDatePickerDialog() {

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = year + "-" + twoDigits(month + 1) + "-" + twoDigits(day);
                editTextBirthDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }
}
