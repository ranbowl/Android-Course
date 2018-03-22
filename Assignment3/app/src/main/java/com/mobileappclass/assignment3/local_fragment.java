package com.mobileappclass.assignment3;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class local_fragment extends Fragment {
    private ArrayList<String> myarray;
    private ArrayAdapter myadapter;
    private View inflaterview = null;
    private ReceiverClassName myreceiver;

    public local_fragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflaterview = inflater.inflate(R.layout.fragment_local_fragment, container, false);
        return inflaterview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DBHelper helper = new DBHelper(getActivity());
        //SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("Test.db", null);

        SQLiteDatabase db = getActivity().openOrCreateDatabase("test.db", MODE_PRIVATE, null);
        helper.onCreate(db);

        ArrayList<String> time = helper.getEntireColumn(helper.DATABASE_COLUMN_TIME);
        ArrayList<String> lat = helper.getEntireColumn(helper.DATABASE_COLUMN_LAT);
        ArrayList<String> lng = helper.getEntireColumn(helper.DATABASE_COLUMN_LONG);

        myarray= new ArrayList<String>();
        for(int i = time.size()-1; i >= 0; i--) {
            String cur = time.get(i) + " " + lat.get(i) + " " + lng.get(i);
            myarray.add(cur);
        }

        myadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, myarray);
        ListView list = (ListView) inflaterview.findViewById(R.id.local_listview);
        list.setAdapter(myadapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction("local_action");
        myreceiver = new ReceiverClassName();
        getActivity().registerReceiver(myreceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();
// unregister receiver
        getActivity().unregisterReceiver(myreceiver);

    }

    private class ReceiverClassName extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String tmp = bundle.getString("local_broad");
            myarray.add(0, tmp);
            myadapter.notifyDataSetChanged();
        }
    }
}
