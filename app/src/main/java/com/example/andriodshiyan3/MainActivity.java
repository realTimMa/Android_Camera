package com.example.andriodshiyan3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements PictureFragment.PictureFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        PictureFragment pictureFragment = PictureFragment.newInstance(null,null);
        transaction.add(R.id.framebox,pictureFragment);
        transaction.commit();
    }

    @Override
    public void setTitle(String title) {
    getSupportActionBar().setTitle(title);
    }
}