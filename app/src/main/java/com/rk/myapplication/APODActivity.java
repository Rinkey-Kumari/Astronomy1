package com.rk.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rk.myapplication.Fragment.FragmentPicOfToday;
import com.rk.myapplication.Fragment.SearchFragment;

public class APODActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_apod_fragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new FragmentPicOfToday()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragement = null;
        switch (item.getItemId()) {
            case(R.id.today) : selectedFragement = new FragmentPicOfToday();
                break;
            case(R.id.search): selectedFragement = new SearchFragment();
                break;

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragement).commit();
        return true;

    }
}