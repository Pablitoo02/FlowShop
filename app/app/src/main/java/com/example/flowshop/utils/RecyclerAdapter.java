package com.example.flowshop.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowshop.R;
import com.example.flowshop.client.OnProductClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    private List<Product> items;
    private OnProductClickListener listener;
    private Context context;

    public RecyclerAdapter(List<Product> items, OnProductClickListener listener) {
        this.items = items;
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_list, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final Product item = items.get(position);
        Picasso.get().load(item.getImage()).into(holder.productImage);
        holder.productName.setText(item.getName());
        holder.productPrice.setText(item.getPrice());
        holder.productBrand.setText(item.getBrand());

        //Método para cuando pulsas el item del RecyclerView, te lleve a él
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.itemClick(item);

            }
        });
    }

    //Método para contar el número de items
    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        else
            return 0;
    }

    //Método para pasar los datos a cada item
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName, productPrice, productBrand;

        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            productImage = itemView.findViewById(R.id.image);
            productName = itemView.findViewById(R.id.name);
            productPrice = itemView.findViewById(R.id.price);
            productBrand = itemView.findViewById(R.id.brand);
        }
    }
}

