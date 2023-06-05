package com.example.flowshop.client;

import static android.app.PendingIntent.getActivity;
import static android.content.Context.MODE_PRIVATE;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.flowshop.R;
import com.example.flowshop.screens.Drawer;
import com.example.flowshop.screens.Login;
import com.example.flowshop.screens.ProfileFragment;
import com.example.flowshop.screens.ReestablishPassword;
import com.example.flowshop.utils.Product;
import com.example.flowshop.utils.RecyclerAdapter;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestClient {
    //URL para utilizar la aplicación desde el emulador
    private String BASE_REAL_URL = "http://10.0.2.2:8000";
    //URL para utilizar la aplicación desde el móvil (tienen que estar conectados a la misma red)
    private String LOCALHOST = "";
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

    public void register(TextView editTextName, TextView editTextSurnames, TextView editTextEmail, TextView editTextPassword) {
        queue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();
        try {

            requestBody.put("name", editTextName.getText().toString());
            requestBody.put("surnames", editTextSurnames.getText().toString());
            requestBody.put("email", editTextEmail.getText().toString());
            requestBody.put("password", editTextPassword.getText().toString());

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
                        Intent intent = new Intent(context, ReestablishPassword.class);
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

    public void reestablishPassword(String passwordToken, String newPassword) {
        queue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("passwordToken", passwordToken);
            requestBody.put("newPassword", newPassword);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/v1/password",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 404) {
                            Toast.makeText(context, "Token no válido", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        this.queue.add(request);
    }

    public void products(String name, int size, int offset, OnProductClickListener productListener, RecyclerView recyclerView, ProductsResponseListener listener) {
        queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL + "/v1/products?size=" + size + "&offset=" + offset + "&name=" + name,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Product> itemList = new ArrayList() {};
                        int count = 0;
                        try {
                            count = response.getInt("count");
                            JSONArray results = response.getJSONArray("results");


                            for (int i=0; i < results.length(); i++) {
                                JSONObject product = results.getJSONObject(i);
                                Product newproduct = new Product(product.getString("name"), product.getString("price"),
                                        product.getString("brand"), product.getString("modelo"), BASE_URL + product.getString("image"));
                                itemList.add(newproduct);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(itemList, productListener);
                        recyclerView.setAdapter(recyclerAdapter);
                        listener.onProductsResponse(count);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        queue.add(request);
    }

    public void product(String modelo, Context context, ImageView image, TextView name, TextView price, TextView brand,
                        TextView description, TextView model, TextView color){

        queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL + "/v1/product/" + modelo,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Picasso.get().load(BASE_URL + response.getString("image")).into(image);
                            name.setText(response.getString("name"));
                            price.setText(response.getString("price"));
                            brand.setText(response.getString("brand"));
                            description.setText(response.getString("description"));
                            model.setText(response.getString("model"));
                            color.setText(response.getString("color"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 404) {
                            Toast.makeText(context, "Este producto no exite", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        this.queue.add(request);
    }

    public void isFavorite(String modelo, IsFavoriteListener listener){

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.GET,
                BASE_URL + "/v1/products/" + modelo + "/favorites",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponseReceived(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResponseReceived(false);
                    }
                },
                context
        );
        this.queue.add(request);
    }

    public void addFavorites(String modelo, ImageButton favoriteButton) {

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.PUT,
                BASE_URL + "/v1/products/" + modelo + "/favorites",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        favoriteButton.setBackgroundResource(R.drawable.full_favorite);
                        favoriteButton.setEnabled(true);//HABILITA EL BOTÓN
                        Toast.makeText(context, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show();
                        favoriteButton.setEnabled(true);//HABILITA EL BOTÓN
                    }
                },
                context
        );
        this.queue.add(request);
    }

    public void deleteFavorites(String modelo, ImageButton favoriteButton) {

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.DELETE,
                BASE_URL + "/v1/products/" + modelo + "/favorites",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        favoriteButton.setBackgroundResource(R.drawable.favorite);
                        favoriteButton.setEnabled(true); //HABILITA EL BOTÓN
                        Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                },
                context
        );

        queue.add(request);
    }

    public void addCart(String modelo) {

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.PUT,
                BASE_URL + "/v1/products/" + modelo + "/cart",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Añadido al carrito", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error al añadir", Toast.LENGTH_SHORT).show();
                    }
                },
                context
        );
        this.queue.add(request);
    }

    public void deleteCart(String modelo) {

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.DELETE,
                BASE_URL + "/v1/products/" + modelo + "/cart",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Eliminado del carrito", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                },
                context
        );

        queue.add(request);
    }

    public void favorites(RecyclerView recyclerView, OnProductClickListener listener){
        queue = Volley.newRequestQueue(context);

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.GET,
                BASE_URL + "/v1/favorites",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Product> itemList = new ArrayList() {};
                        try {
                            JSONArray results = response.getJSONArray("favorites");

                            for (int i = results.length() - 1; i >= 0; i--) {
                                JSONObject product = results.getJSONObject(i);
                                Product newproduct = new Product(product.getString("product__name"), product.getString("product__price"),
                                        product.getString("product__brand"), product.getString("product__modelo"), BASE_URL + product.getString("product__image"));
                                itemList.add(newproduct);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LinearLayoutManager llm = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(llm);
                        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(itemList, listener);
                        recyclerView.setAdapter(recyclerAdapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                context
        );
        queue.add(request);
    }

    public void profile(TextView name, TextView surnames, TextView email){
        queue = Volley.newRequestQueue(context);

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.GET,
                BASE_URL + "/v1/profile",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            name.setText(response.getString("name"));
                            surnames.setText(response.getString("surnames"));
                            email.setText(response.getString("email"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                },
                context
        );

        this.queue.add(request);
    }

    public void editProfile(EditText name, EditText surnames, EditText email){

        queue = Volley.newRequestQueue(context);

        JSONObject body = new JSONObject();
        try {
            body.put("name", name.getText().toString());
            body.put("surnames", surnames.getText().toString());
            body.put("email", email.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.PUT,
                BASE_URL + "/v1/profile",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "CAMBIOS GUARDADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("fragment", "profile");
                        Intent intent = new Intent(context, Drawer.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                },
                context
        );

        this.queue.add(request);
    }

    public void cart(TextView total, RecyclerView recyclerView, OnProductClickListener listener){
        queue = Volley.newRequestQueue(context);

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.GET,
                BASE_URL + "/v1/cart",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Product> itemList = new ArrayList() {};
                        try {
                            JSONArray results = response.getJSONArray("products_cart");
                            //La suma de los precios se graba en una variable de tipo double
                            //para poder limitar los decimales a dos
                            double totalPrice = response.getDouble("total_price");
                            String formattedPrice = String.format("%.2f", totalPrice);
                            total.setText(formattedPrice);

                            for (int i = results.length() - 1; i >= 0; i--) {
                                JSONObject product = results.getJSONObject(i);
                                Product newproduct = new Product(product.getString("product__name"), product.getString("product__price"),
                                        product.getString("product__brand"), product.getString("product__modelo"), BASE_URL + product.getString("product__image"));
                                itemList.add(newproduct);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LinearLayoutManager llm = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(llm);
                        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(itemList, listener);
                        recyclerView.setAdapter(recyclerAdapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                context
        );
        queue.add(request);
    }

    public void payment(TextView total){
        queue = Volley.newRequestQueue(context);

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.GET,
                BASE_URL + "/v1/cart",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Product> itemList = new ArrayList() {};
                        try {
                            //La suma de los precios se graba en una variable de tipo double
                            //para poder limitar los decimales a dos
                            double totalPrice = response.getDouble("total_price");
                            String formattedPrice = String.format("%.2f", totalPrice);
                            total.setText(formattedPrice);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                context
        );
        queue.add(request);
    }

    public void deleteCartList(){
        queue = Volley.newRequestQueue(context);

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.DELETE,
                BASE_URL + "/v1/cart",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                context
        );
        queue.add(request);
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