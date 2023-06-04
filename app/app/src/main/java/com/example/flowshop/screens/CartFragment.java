package com.example.flowshop.screens;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowshop.R;
import com.example.flowshop.client.OnProductClickListener;
import com.example.flowshop.client.RestClient;
import com.example.flowshop.utils.Product;
import com.example.flowshop.utils.RecyclerAdapter;

import java.util.List;

public class CartFragment extends Fragment implements OnProductClickListener {

    private Context context;
    private TextView total;
    private RecyclerView cart_recycler;
    private RecyclerAdapter cartAdapter;
    private List<Product> items;
    private RestClient restClient;

    public static Fragment newInstance() {
        CartFragment myFragment = new CartFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        context = getActivity().getApplicationContext();

        total = view.findViewById(R.id.total);

        cart_recycler = view.findViewById(R.id.recyclerViewcart);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        cart_recycler.setLayoutManager(manager);

        cartAdapter = new RecyclerAdapter(items, this);
        cart_recycler.setAdapter(cartAdapter);

        peticion();

        return view;
    }

    //Instancia de la petición de "RestClient" para que muestre los items
    private void peticion() {
        restClient = RestClient.getInstance(context);
        restClient.cart(total, cart_recycler, this);
    }

    // Método que llama cada vez que se se hace click en un item
    @Override
    public void itemClick(Product item) {
        getActivity().getFragmentManager().beginTransaction().replace(R.id.frameContainer, DetailFragment.newInstance(item.getModelo())).commit();
    }
}