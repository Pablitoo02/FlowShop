package com.example.flowshop.screens;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flowshop.R;

public class DetailSweatshirtFragment extends Fragment {

    private String modelo;

    private static final String ARG_PARAM1 = "param1";

    public DetailSweatshirtFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String param1) {
        DetailSweatshirtFragment fragment = new DetailSweatshirtFragment();
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
        return inflater.inflate(R.layout.fragment_detail_sweatshirt, container, false);
    }

}