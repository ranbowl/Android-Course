package com.example.xinyu.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Fragemnt_a extends Fragment {
    private View inflaterview = null;
    private ArrayList<Map<String, String>> myarray = new ArrayList<Map<String, String>>();
    private ArrayList<String> array_1;
    private ArrayList<String> array_2;
    private ArrayAdapter myadapter;
    private ListView my_list;
    private ListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflaterview = inflater.inflate(R.layout.fragment_fragemnt_a, container, false);
        return inflaterview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        EditText et1 = (EditText) getActivity().findViewById(R.id.A_ET1);
        EditText et2 = (EditText) getActivity().findViewById(R.id.A_ET2);

        final String name = et1.getText().toString();
        final String time = et2.getText().toString();

        my_list = (ListView) getActivity().findViewById(R.id.a_listview) ;
        array_1 = new ArrayList<>();
        array_2 = new ArrayList<>();
        myadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,array_1);
        my_list.setAdapter(myadapter);


        //COnfirm button
        Button bt = (Button)getActivity().findViewById(R.id.Confirm_bt);



        bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // ArrayList<HashMap<String,String>> myarray = new ArrayList<HashMap<String, String>>();
//                HashMap<String,String> map = new HashMap<String, String>();
//                map.put("ItemTitle", name);
//                map.put("ItemText", time);
//                adapter.add(map);

                EditText et1 = (EditText) getActivity().findViewById(R.id.A_ET1);
                EditText et2 = (EditText) getActivity().findViewById(R.id.A_ET2);

                final String name = et1.getText().toString();
                final String time = et2.getText().toString();
                array_1.add(name);
                array_2.add(time);

                myadapter.notifyDataSetChanged();
                MyTask mTask = new MyTask();
                String s = name + "," + time;
                mTask.execute(s);

            }
        });




        my_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EditText et1 = (EditText) getActivity().findViewById(R.id.A_ET1);
                EditText et2 = (EditText) getActivity().findViewById(R.id.A_ET2);

                 String name = et1.getText().toString();
                 String time = et2.getText().toString();
                Fragment fragment_b=(Fragment)getFragmentManager().findFragmentById(R.id.main_fragment);
                fragment_b = Fragment_b.newInstance(name, time, array_1, array_2);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment,fragment_b).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

            }
        });


    }




    private class MyTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String...x)
        {
            //get time


            final  String[] o = x[0].split(",");
            final int y = Integer.parseInt(o[1]);
            final Thread thread = new Thread(new Runnable() {
                public void run() {

                    try {
                        TimeUnit.SECONDS.sleep(y);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String tmp = " Task" + o[0] + "has completed in " + y  + "seconds";
                    Toast.makeText(getActivity(), tmp, Toast.LENGTH_SHORT).show();
                }


            });

            thread.start();




            return null;
        }

    }


}
