package com.example.xinyu.assignment_1;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.view.LayoutInflater;


import java.util.ArrayList;
import java.util.Map;


/**
 * Created by Xinyu on 2016/10/5.
 */



public class ListAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater = null;
    private ArrayList<Map<String, String>> myarray;

    public ListAdapter(Context context, ArrayList<Map<String, String>> data) {
        super(context,R.layout.my_listitem, data);
        this.context = context;
        this.myarray = data;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.my_listitem, parent, false);

        TextView Title = (TextView) (convertView.findViewById(R.id.inner_layout)).findViewById(R.id.ItemTitle);
        TextView Text = (TextView) (convertView.findViewById(R.id.inner_layout)).findViewById(R.id.ItemText);
        CheckBox CB = (CheckBox) convertView.findViewById(R.id.checkbox);
        Title.setText(myarray.get(position).get("ItemTitle"));
        Text.setText(myarray.get(position).get("ItemText"));
        CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true) {
                    Object removeLine = getItem(position);
                    remove(removeLine);
                }
            }
        });



        return convertView;
    }


}
