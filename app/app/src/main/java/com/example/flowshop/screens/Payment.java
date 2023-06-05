package com.example.flowshop.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowshop.Launcher;
import com.example.flowshop.R;
import com.example.flowshop.client.RestClient;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class Payment extends AppCompatActivity  {

    private Context context;
    private RestClient restClient;
    private TextView amount;
    private Button payment;

    String clientId = "AdHHerfGV-S2v9r2hjMB5AXqxu5LP9UU3h-wjGGNXWerbT189LR3dXFO3HPXHL-YLWSRg1tvPAFRL4Fm";

    int PAYPAL_REQUEST_CODE = 123;

    public static PayPalConfiguration configuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        amount = findViewById(R.id.amount);
        payment = findViewById(R.id.payment);


        configuration = new PayPalConfiguration().environment
                (PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(clientId);

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayment();
            }
        });

        peticion();


    }

    private void getPayment() {
        String amounts = amount.getText().toString();

        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amounts)),
                "EUR", "Código con Arvind", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            PaymentConfirmation paymentConfirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

            if (paymentConfirmation != null) {
                try {
                    String paymentDetails = paymentConfirmation.toJSONObject().toString();
                    JSONObject object = new JSONObject(paymentDetails);
                    deletePeticion();

                } catch (JSONException e) {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "Pago inválido", Toast.LENGTH_LONG).show();
        }
    }

    //Instancia de la petición de "RestClient" para que muestre los items
    private void peticion() {
        restClient = RestClient.getInstance(context);
        restClient.payment(amount);
    }

    //Instancia de la petición de "RestClient" para que muestre los items
    private void deletePeticion() {
        restClient = RestClient.getInstance(context);
        restClient.deleteCartList();
    }

    // Método volver a la pantalla de Launcher
    @Override
    public void onBackPressed(){
        startActivity(new Intent(Payment.this, Drawer.class));
    }
}
