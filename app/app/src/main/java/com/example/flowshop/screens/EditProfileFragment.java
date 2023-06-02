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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flowshop.R;
import com.example.flowshop.client.RestClient;
import com.example.flowshop.utils.ValidateEmail;

public class EditProfileFragment extends Fragment {

    private Context context;
    private Button save;
    private RestClient restClient;
    private EditText name, surnames,email;

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        name = view.findViewById(R.id.name);
        surnames = view.findViewById(R.id.surnames);
        email = view.findViewById(R.id.email);

        save = view.findViewById(R.id.save);

        //Botón guardar los cambios de los datos del usuario
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0) {
                    name.setError("Campo obligatorio");
                }if (surnames.getText().length() == 0) {
                    surnames.setError("Campo obligatorio");
                }if (email.getText().length() == 0) {
                    email.setError("Campo obligatorio");
                } else if(!ValidateEmail.validateEmail(email.getText().toString())) {
                    email.setError("Email no valido");
                }if(surnames.getError() == null && surnames.getError() == null){
                    restClient.editProfile(name, surnames, email);
                }


            }
        });

        peticionGet();
        return view;
    }

    //Instancia de la petición de "RestClient" para que muestre los datos de usuario
    private void peticionGet() {
        restClient = RestClient.getInstance(context);
        restClient.profile(name, surnames ,email);
    }
}