package com.mobileappclass.assignment3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Fragment cur_fragment;
    private local_fragment l_fragment;
    private remote_fragment r_fragment;
    private query_fragment q_fragment;
    private Intent myintent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);



        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

//            if(savedInstanceState == null) {
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                if(l_fragment == null) {
//                    l_fragment = new local_fragment();
//                }
//                cur_fragment = l_fragment;
//                ft.replace(R.id.main_fragment, l_fragment).commit();
//            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            l_fragment = new local_fragment();
            cur_fragment = l_fragment;
            ft.replace(R.id.main_fragment, l_fragment).commit();
        }

        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            l_fragment = new local_fragment();
            r_fragment = new remote_fragment();
            ft.replace(R.id.left_Frame, l_fragment).replace(R.id.right_Frame, r_fragment).commit();
        }


        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myintent = new Intent(this, MyService.class);
            startService(myintent);
        }

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            myintent = new Intent(this, MyService.class);
//            startService(myintent);
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("tag","service");
        this.stopService(myintent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //noinspection SimplifiableIfStatement
        if (id == R.id.online) {

            if(r_fragment == null) {
                r_fragment = new remote_fragment();
            }
            if(!r_fragment.isAdded()) {
                ft.hide(cur_fragment).add(R.id.main_fragment, r_fragment).commit();
                cur_fragment = r_fragment;
            }
            else {
                ft.hide(cur_fragment).show(r_fragment).commit();
                cur_fragment = r_fragment;
            }
            return true;
        }

        if (id == R.id.offline) {

            if(l_fragment == null) {
                l_fragment = new local_fragment();
            }
            if(!l_fragment.isAdded()) {
                ft.hide(cur_fragment).add(R.id.main_fragment, l_fragment).commit();
                cur_fragment = l_fragment;
            }
            else {
                ft.hide(cur_fragment).show(l_fragment).commit();
                cur_fragment = l_fragment;
            }
            return true;
        }

        if (id == R.id.query) {

            if(q_fragment == null) {
                q_fragment = new query_fragment();
            }
            if(!q_fragment.isAdded()) {
                ft.hide(cur_fragment).add(R.id.main_fragment, q_fragment).commit();
                cur_fragment = q_fragment;
            }
            else {
                ft.hide(cur_fragment).show(q_fragment).commit();
                cur_fragment = q_fragment;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
