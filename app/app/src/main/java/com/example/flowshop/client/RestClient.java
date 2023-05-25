package com.example.flowshop.client;

import static android.content.Context.MODE_PRIVATE;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.flowshop.screens.Drawer;
import com.example.flowshop.screens.Login;
import com.example.flowshop.screens.Reestablish;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RestClient {
    //URL para utilizar la aplicación desde el emulador
    private String BASE_REAL_URL = "http://10.0.2.2:8000";
    //URL para utilizar la aplicación desde el móvil (tienen que estar conectados a la misma red)
    private String LOCALHOST = "http://192.168.83.142:8000";
    //Para elegir la URL a usar
    private String BASE_URL = BASE_REAL_URL;

    private Context context;

    private RequestQueue queue;

    private RestClient(Context context) {
        this.context = context;
    }

    //Para instanciar esta clase
    private static RestClient singleton = null;
    public static RestClient getInstance(Context context) {
        if (singleton == null) {
            singleton = new RestClient(context);
        }
        return singleton;
    }

    public void isLogged(String sessionToken) {
        queue = Volley.newRequestQueue(context);

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.GET,
                BASE_URL + "/v1/logged",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(context, Drawer.class);
                        Toast.makeText(context, "¡Logueado con éxito!", Toast.LENGTH_LONG).show();
                        context.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                        preferences.edit().remove("tokenSession").commit();

                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                    }
                },
                context
        );

        this.queue.add(request);
    }

    public void register(TextView editTextName, TextView editTextSurnames, TextView editTextEmail, TextView editTextPassword, TextView editTextPassword2, TextView editTextBirthDate) {
        queue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();
        try {

            requestBody.put("name", editTextName.getText().toString());
            requestBody.put("surnames", editTextSurnames.getText().toString());
            requestBody.put("email", editTextEmail.getText().toString());
            requestBody.put("password", editTextPassword.getText().toString());
            requestBody.put("birthdate", editTextBirthDate.getText().toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/v1/register",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            Toast.makeText(context, "Sin conexión", Toast.LENGTH_LONG).show();

                        } else if(error.networkResponse.statusCode == 409) {
                            Toast.makeText(context, "Cuenta ya registrada", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(context, "Error: " + serverCode, Toast.LENGTH_LONG).show();

                        }
                    }
                });
        this.queue.add(request);
    }

    public void login(EditText email, EditText password, Context context) {
        queue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email.getText().toString());
            requestBody.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/v1/login",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Guardando el id del usuario en las sharedPreferences
                        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        try {
                            editor.putString("tokenSession", response.getString("sessionToken"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.apply();
                        Intent intent = new Intent(context, Drawer.class);
                        context.startActivity(intent);

                        try {
                            Toast.makeText(context, response.getString("tokenSession"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (error.networkResponse.statusCode) {
                            case 404:
                                email.setError("Usuario no registrado");
                                break;
                            case 401:
                                password.setError("Contraseña incorrecta");
                        }
                    }
                });
        queue.add(request);
    }

    public void ForgottenPassword(EditText email, Context context){
        queue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("email", email.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/v1/forget",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Código enviado", Toast.LENGTH_SHORT).show();
                        //CAMBIO A LA SIGUIENTE PANTALLA
                        Intent intent = new Intent(context, Reestablish.class);
                        context.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse.statusCode == 404) {
                            email.setError("Usuario no registrado");
                        }
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DEFAULT_BACKOFF_MULT));
        this.queue.add(request);
    }
}



class JsonObjectRequestWithCustomAuth extends JsonObjectRequest {
    private Context context;

    public JsonObjectRequestWithCustomAuth(int method,
                                           String url,
                                           @Nullable JSONObject jsonRequest,
                                           Response.Listener<JSONObject> listener,
                                           @Nullable Response.ErrorListener errorListener,
                                           Context context) {
        super(method, url, jsonRequest, listener, errorListener);
        this.context = context;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String sessionToken = preferences.getString("tokenSession", null);
        //PARA PROBAR LA PANTALLA
        /*String sessionToken = "";*/

        HashMap<String, String> myHeaders = new HashMap<>();
        myHeaders.put("Token", sessionToken);
        return myHeaders;
    }
}