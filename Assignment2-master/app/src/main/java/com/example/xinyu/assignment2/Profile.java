package com.example.xinyu.assignment2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {
    //private Profile_adpter adapter;
    private ArrayList<Map<String, String>> pro_array = new ArrayList<Map<String, String>>();
    private List<String> rel_array = new ArrayList<String>();
    private ListView pro_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        String name = intent.getStringExtra("user_name").toString();

        TextView pro_name = (TextView) findViewById(R.id.Profile_input_name);
        pro_name.setText(name);
        TextView pro_phone = (TextView) findViewById(R.id.Profile_input_phone);
        SharedPreferences names = getSharedPreferences("user_phone", MODE_PRIVATE);
        Map<String, ?> allEntries = names.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String user_name = entry.getKey();
            if(user_name.toString().equals(name.toString())) {
                String user_information = entry.getValue().toString();
                pro_phone.setText(user_information);
                break;
            }
        }

        //read user information

        SharedPreferences user_rel = getSharedPreferences("user_relation", MODE_PRIVATE);
        Map<String, ?> allEntries2 = user_rel.getAll();
        String user_relation = new String();
        for (Map.Entry<String, ?> entry : allEntries2.entrySet()) {
            String user_name = entry.getKey();
            if(user_name.toString().equals(name.toString())) {
                user_relation = entry.getValue().toString();
                break;
            }
        }

        String[] relation = user_relation.split(",");
        for(int i=0;i<relation.length;i++){
            rel_array.add(relation[i]);
        }


        pro_listview = (ListView) findViewById(R.id.Profile_list) ;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rel_array);
        pro_listview.setAdapter(adapter);


        pro_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_profile = new Intent(view.getContext(), Profile.class);
                intent_profile.putExtra("user_name", rel_array.get(position).toString());
                startActivity(intent_profile);
            }
        });

    }



}
