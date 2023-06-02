package com.example.flowshop.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flowshop.R;
import com.example.flowshop.client.RestClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private Context context;
    private Button edit_profile,logout;
    private RestClient restClient;
    private TextView name, surnames,email;

    public static Fragment newInstance() {
        ProfileFragment myFragment = new ProfileFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        context = getActivity().getApplicationContext();

        name = view.findViewById(R.id.name);
        surnames = view.findViewById(R.id.surnames);
        email = view.findViewById(R.id.email);

        peticion();

        logout = view.findViewById(R.id.logout);

        //Botón cerrar la sesión del usuario
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(context,Login.class);
                startActivity(intent);

            }
        });

        edit_profile = view.findViewById(R.id.editProfile);

        //Botón para editar los datos de usuario
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.frameContainer, EditProfileFragment.newInstance()).commit();
            }
        });

        return view;
    }

    //Instancia de la petición de "RestClient" para que muestre los datos de usuario
    private void peticion() {
        restClient = RestClient.getInstance(context);
        restClient.profile(name, surnames ,email);
    }
}