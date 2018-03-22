package com.mobileappclass.assignment3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class query_fragment extends Fragment {
    private String cur_sorting;
    private ArrayList<String> myarray = new ArrayList<String>();
    private ArrayAdapter myadapter;
    private View inflaterview = null;


    public query_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflaterview = inflater.inflate(R.layout.fragment_query, container, false);
        Spinner spinner = (Spinner) inflaterview.findViewById(R.id.query_spinner);
        String[] mItems = getResources().getStringArray(R.array.order);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner .setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] order = getResources().getStringArray(R.array.order);
                cur_sorting = order[pos].toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        return inflaterview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);





        myadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,myarray);
        ListView list = (ListView) inflaterview.findViewById(R.id.query_listview);
        list.setAdapter(myadapter);

        Button bt = (Button) getActivity().findViewById(R.id.query_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) getActivity().findViewById(R.id.query_ET1);
                String netid = et.getText().toString();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students");
                DatabaseReference students = ref.child(netid);

                students.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String information = new String();
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            information = information  + child.getValue().toString() + " ";
                        }
                        if(cur_sorting.equals("Descending")) {
                            myarray.add(0, information);
                            myadapter.notifyDataSetChanged();
                        }
                        else if(cur_sorting.equals("Ascending")) {
                            myarray.add(information);
                            myadapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        String information = new String();
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            information = information  + child.getValue().toString() + " ";
                        }

                        if(cur_sorting.equals("Descending")) {
                            myarray.add(0, information);
                            myadapter.notifyDataSetChanged();
                        }
                        else if(cur_sorting.equals("Ascending")) {
                            myarray.add(information);
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
        });

    }
}
