package com.example.xinyu.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
private Fragemnt_a A_fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        A_fragment = new Fragemnt_a();
        ft.replace(R.id.main_fragment, A_fragment).commit();

    }
}
