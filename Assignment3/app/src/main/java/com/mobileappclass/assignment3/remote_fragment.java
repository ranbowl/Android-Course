package com.mobileappclass.assignment3;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class remote_fragment extends Fragment {
    private ArrayList<String> myarray = new ArrayList<String>();
    private ArrayAdapter myadapter;
    private View inflaterview = null;
    private MyTask mTask;
    private Activity myactivity;
    //private FirebaseDatabase db;


    public remote_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myactivity = getActivity();
        inflaterview = inflater.inflate(R.layout.fragment_remote_fragment, container, false);
        return inflaterview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkReceiver receiver = new NetworkReceiver();
        getActivity().registerReceiver(receiver, filter);

        Button bt = (Button) inflaterview.findViewById(R.id.remote_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper helper = new DBHelper(getActivity());
                SQLiteDatabase s_db = getActivity().openOrCreateDatabase("test.db", MODE_PRIVATE, null);
                helper.onCreate(s_db);

                final ArrayList<String> time = helper.getEntireColumn(helper.DATABASE_COLUMN_TIME);
                final ArrayList<String> lat = helper.getEntireColumn(helper.DATABASE_COLUMN_LAT);
                final ArrayList<String> lng = helper.getEntireColumn(helper.DATABASE_COLUMN_LONG);
                s_db.close();

                for(int i = 0; i < time.size(); i++) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students"); // What database can I actually talk to?
                    DatabaseReference students = ref.child("xl344");
                    DatabaseReference tmp = students.child(time.get(i));
                    tmp.child("date").setValue(time.get(i));
                    tmp.child("x").setValue(lat.get(i));
                    tmp.child("y").setValue(lng.get(i));
                    tmp.child("netID").setValue("xl344");
                }

                EditText et_con = (EditText) getActivity().findViewById(R.id.remote_ET1);
                et_con.setText("Connected");
            }
        });
        myadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,myarray);
        ListView list = (ListView) inflaterview.findViewById(R.id.remote_listview);
        list.setAdapter(myadapter);

        read_data();





    }


    public void read_data() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();
//                List<Object> values = new ArrayList<Object>(td.values());

                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String information = new String();
                    for(DataSnapshot tmp : child.getChildren()) {
                        information = information  + tmp.getValue().toString() + " ";
                    }
                    myarray.add(0, information);
                    if(myarray.size() > 200) {
                        myarray.remove(200);
                    }
                    myadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String information = new String();
                    for(DataSnapshot tmp : child.getChildren()) {
                        information = information  + tmp.getValue().toString() + " ";
                    }
                    myarray.add(0, information);
                    if(myarray.size() > 200) {
                        myarray.remove(200);
                    }
                    myadapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class MyTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void...x)
        {
            final DBHelper helper=new DBHelper(myactivity);
            SQLiteDatabase db = myactivity.openOrCreateDatabase("test.db", MODE_PRIVATE, null);
            helper.onCreate(db);
            ArrayList<String> time = helper.getEntireColumn(helper.DATABASE_COLUMN_TIME);
            ArrayList<String> lat = helper.getEntireColumn(helper.DATABASE_COLUMN_LAT);
            ArrayList<String> lng = helper.getEntireColumn(helper.DATABASE_COLUMN_LONG);

            ArrayList<String> locationlist= new ArrayList<String>();
            for(int i = time.size()-1; i >= 0; i--) {
                String cur = time.get(i) + " " + lat.get(i) + " " + lng.get(i);
                locationlist.add(cur);
            }

            for(int i = 0; i < time.size(); i++) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students"); // What database can I actually talk to?
                DatabaseReference students = ref.child("xl344");
                DatabaseReference tmp = students.child(time.get(i));
                tmp.child("date").setValue(time.get(i));
                tmp.child("x").setValue(lat.get(i));
                tmp.child("y").setValue(lng.get(i));
                tmp.child("netID").setValue("xl344");
            }
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("local_action");
//            myactivity.registerReceiver(new remote_fragment.ReceiverClass(), filter);



            return null;
        }

    }

    private class ReceiverClass extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Bundle bundle=intent.getExtras();
//            String tmp = bundle.getString("local_broad");
//            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Students");
//            DatabaseReference students = dr.child("xl344");
//            String[] info = tmp.split(" ");
//
//            DatabaseReference cur = students.child(info[0]);
//            cur.child("date").setValue(info[0]);
//            cur.child("x").setValue(info[1]);
//            cur.child("y").setValue(info[2]);
//            cur.child("netID").setValue("xl344");
            mTask = new MyTask();
            mTask.execute();
        }
    }

    public class NetworkReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            EditText et = (EditText) inflaterview.findViewById(R.id.remote_ET2);
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connManager.getActiveNetworkInfo();

            if(info == null) {
                et.setText("No Network");
                EditText et_con = (EditText) inflaterview.findViewById(R.id.remote_ET1);
                et_con.setText("UnConnected");
            }
            else if(info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiMgr = (WifiManager) myactivity.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                et.setText("Wifi:" + wifiInfo.getSSID().toString());

                mTask = new MyTask();
                mTask.execute();
                EditText et_con = (EditText) inflaterview.findViewById(R.id.remote_ET1);
                et_con.setText("Connected");
                IntentFilter filter = new IntentFilter();
                filter.addAction("local_action");
                ReceiverClass myreceiver = new ReceiverClass();
                getActivity().registerReceiver(myreceiver, filter);
            }
            else {
                et.setText("Mobile Data");
            }
        }
    }
}
