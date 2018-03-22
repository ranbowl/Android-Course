package com.example.xinyu.myapplication;

/**
 * Created by Xinyu on 2016/11/8.
 */

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
        super(context,R.layout.row, data);
        this.context = context;
        this.myarray = data;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.A_ET1);
        TextView time = (TextView) convertView.findViewById(R.id.A_ET2);

        name.setText(myarray.get(position).get("ItemTitle"));
        time.setText(myarray.get(position).get("ItemText"));




        return convertView;
    }


}
