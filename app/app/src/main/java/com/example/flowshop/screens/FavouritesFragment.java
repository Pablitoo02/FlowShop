package com.example.flowshop.screens;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class FavouritesFragment extends Fragment implements OnProductClickListener {
    private Context context;
    private RecyclerView fav_recycler;
    private RecyclerAdapter recyclerAdapter;
    private List<Product> items;
    private RestClient restClient;

    public static Fragment newInstance() {
        FavouritesFragment myFragment = new FavouritesFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        context = getActivity().getApplicationContext();

        fav_recycler = view.findViewById(R.id.recyclerViewfav);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        fav_recycler.setLayoutManager(manager);

        recyclerAdapter = new RecyclerAdapter(items, this);
        fav_recycler.setAdapter(recyclerAdapter);

        peticion();

        return view;
    }

    //Instancia de la petición de "RestClient" para que muestre los items
    private void peticion() {
        restClient = RestClient.getInstance(context);
        restClient.favorites(fav_recycler, this);
    }

    // Método que llama cada vez que se se hace click en un item
    @Override
    public void itemClick(Product item) {
        getActivity().getFragmentManager().beginTransaction().replace(R.id.frameContainer, DetailFragment.newInstance(item.getModelo())).commit();
    }
}
