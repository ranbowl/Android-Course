package com.example.xinyu.myapplication;



import android.content.Context;
import android.net.Uri;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_b extends Fragment {

    private ArrayList<String> array_1;
    private ArrayList<String> array_2;
    private ArrayAdapter myadapter;
    private ListView my_list;
    public Fragment_b() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_b, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String name = getArguments().getString("name");
        String time = getArguments().getString("time");
        array_1 = getArguments().getStringArrayList("list1");

        array_2 = getArguments().getStringArrayList("list2");
        TextView tv1 = (TextView) getActivity().findViewById(R.id.B_T1);
        TextView tv2 = (TextView) getActivity().findViewById(R.id.B_T2);

        tv1.setText(name);
        tv2.setText(time);

        my_list = (ListView) getActivity().findViewById(R.id.a_listview) ;
        array_1 = new ArrayList<>();
        array_2 = new ArrayList<>();
        myadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,array_1);
        my_list.setAdapter(myadapter);

        Button bt = (Button) getActivity().findViewById(R.id.B_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment_a=(Fragment)getFragmentManager().findFragmentById(R.id.main_fragment);
                fragment_a = new Fragemnt_a();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment,fragment_a).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

            }
        });

    }

    public  static Fragment_b newInstance(String name, String time, ArrayList<String> s, ArrayList<String> s2)
    {
        Fragment_b tmp = new Fragment_b();
        Bundle args = new Bundle();
        args.putString("time", time);
        args.putString("name", name);
        args.putStringArrayList("list1", s);
        args.putStringArrayList("list2", s2);

        tmp.setArguments(args);
        return tmp;
    }

}
