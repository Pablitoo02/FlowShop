package com.example.flowshop.screens;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.example.flowshop.R;
import com.example.flowshop.client.IsFavoriteListener;
import com.example.flowshop.client.RestClient;

public class DetailFragment extends Fragment {

    private Context context;
    private RestClient restClient;

    boolean buttonStatus;

    private TextView name, price, brand, description, model, color;
    private ImageView image;
    private ImageButton favorite;
    private Button cart;
    private String modelo;

    private static final String ARG_PARAM1 = "param1";


    public DetailFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String param1) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelo = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        image = view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        price = view.findViewById(R.id.price);
        brand = view.findViewById(R.id.brand);
        description = view.findViewById(R.id.description);
        model = view.findViewById(R.id.model);
        color = view.findViewById(R.id.color);

        context = getActivity().getApplicationContext();
        restClient = RestClient.getInstance(context);
        restClient.product(modelo, context, image, name, price, brand, description, model, color);

        favorite = view.findViewById(R.id.favorite);
        favorite.setOnClickListener(favoriteListener);

        cart = view.findViewById(R.id.cart);

        //Método para que, al pulsar el botón de "AÑADIR AL CARRITO",
        // el producto se añada al carrito
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restClient.addCart(modelo, cart);
            }
        });

        //Método que recibe si el producto está o no guardado en favoritos
        //y pone el icono correespondiente
        restClient.isFavorite(modelo, new IsFavoriteListener() {
            @Override
            public void onResponseReceived(boolean isFavorite) {
                if (isFavorite == true){
                    favorite.setBackgroundResource(R.drawable.full_favorite);
                    buttonStatus = true;
                }else{
                    favorite.setBackgroundResource(R.drawable.favorite);
                    buttonStatus = false;
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    //Método para que, al pulsar el botón de "FAVORITOS", el producto
    //se añada a favoritos, si no lo está,
    //y se eliminé de favoritos, si lo está
    private View.OnClickListener favoriteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (buttonStatus == true){
                buttonStatus = false;
                favorite.setEnabled(false); //DESHABILITA EL BOTÓN
                restClient.deleteFavorites(modelo, favorite);
                //ELIMINAR FAVORITOS
            }else{
                buttonStatus = true;
                favorite.setEnabled(false); //DESHABILITA EL BOTÓN
                restClient.addFavorites(modelo, favorite);
                //AÑADIR FAVORITOS
            }
        }
    };
}