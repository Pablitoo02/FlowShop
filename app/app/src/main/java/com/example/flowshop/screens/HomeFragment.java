package com.example.flowshop.screens;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.flowshop.R;

public class HomeFragment extends Fragment {

    private Context context;
    private Button sweatshirts, t_shirts, trousers;

    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    public HomeFragment() {
        // Required empty public constructor
    }

    /*public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        sweatshirts =  view.findViewById(R.id.sweatshirts);
        t_shirts =  view.findViewById(R.id.t_shirts);
        trousers =  view.findViewById(R.id.trousers);

        //Botón "Sweatshirts"
        sweatshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambio al fragment "Sweatshirts"
                SweatshirtsFragment sweatshirts = new SweatshirtsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameContainer,sweatshirts)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Botón "T_shirts"
        t_shirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambio al fragment "T_shirts"
                T_shirtsFragment t_shirts = new T_shirtsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameContainer,t_shirts)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Botón "Trousers"
        trousers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambio al fragment "Trousers"
                TrousersFragment trousers=new TrousersFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameContainer,trousers)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}