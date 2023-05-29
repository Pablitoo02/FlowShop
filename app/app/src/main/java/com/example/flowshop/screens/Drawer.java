package com.example.flowshop.screens;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.flowshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Drawer extends AppCompatActivity {

    //Declaración de los fragments
    HomeFragment homeFragment = new HomeFragment();
    FavouritesFragment favouritesFragment = new FavouritesFragment();
    CartFragment cartFragment = new CartFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Para que se abra el "HomeFragment" desde el principio
        loadFragment(homeFragment);
    }

    //Método para mostrar cada fragment
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    loadFragment(homeFragment);
                    return true;
                case R.id.favourites:
                    loadFragment(favouritesFragment);
                    return true;
                case R.id.cart:
                    loadFragment(cartFragment);
                    return true;
                case R.id.profile:
                    loadFragment(profileFragment);
                    return true;
            }
            return false;
        }
    };

    //Método para cambiar de fragment
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.commit();
    }
}
