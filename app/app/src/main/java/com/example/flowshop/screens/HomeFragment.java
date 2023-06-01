package com.example.flowshop.screens;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.flowshop.R;
import com.example.flowshop.client.OnProductClickListener;
import com.example.flowshop.client.RestClient;
import com.example.flowshop.client.ProductsResponseListener;
import com.example.flowshop.utils.Product;
import com.example.flowshop.utils.RecyclerAdapter;

import java.util.List;

public class HomeFragment extends Fragment implements OnProductClickListener, SearchView.OnQueryTextListener, ProductsResponseListener {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RestClient restClient;
    private List<Product> items;
    private Context context;
    private Button previous, next;
    private final int size = 5;
    private int offset = 0;
    private String query= "";

    public static Fragment newInstance() {
        HomeFragment myFragment = new HomeFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);


        recyclerView = view.findViewById(R.id.recyclerView);

        context = getActivity().getApplicationContext();

        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        recyclerAdapter = new RecyclerAdapter(items, this);
        recyclerView.setAdapter(recyclerAdapter);

        peticion(query);

        previous = view.findViewById(R.id.previous);
        next = view.findViewById(R.id.next);

        //Botón para pasar a la next página
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offset != 0) {
                    offset-=size;
                    peticion(query);
                }
            }
        });

        //Botón para volver a la página anterior
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerAdapter.getItemCount() == 0) {
                    offset+=size;
                    peticion(query);
                }
            }
        });

        return view;
    }

    //Instancia de la petición de "RestClient" para que muestre los items
    private void peticion(String query) {
        restClient = RestClient.getInstance(context);
        restClient.products(query, size, offset,this, recyclerView, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // Método que llama cada vez que se escribe o borra un caracter
    @Override
    public boolean onQueryTextChange(String query) {
        offset = 0;
        this.query = query;
        peticion(query);
        return false;
    }

    // Método que llama cada vez que se se hace click en un item
    public void itemClick(Product item) {
        getActivity().getFragmentManager().beginTransaction().replace(R.id.frameContainer, DetailFragment.newInstance(item.getModelo())).commit();
    }

    //Método para que los botones funcionen cuando deben
    @Override
    public void onProductsResponse(int count) {
        next.setEnabled(count > offset+size);
        previous.setEnabled(offset!=0);
    }
}