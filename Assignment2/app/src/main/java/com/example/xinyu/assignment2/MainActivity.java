package com.example.xinyu.assignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Mainlist_adpter adapter;
    private ArrayList<Map<String, String>> myarray = new ArrayList<Map<String, String>>();
    private ListView main_listview;
    public ArrayList delete_list = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            main_listview = (ListView) findViewById(R.id.main_list);
            main_listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        String phone = intent.getStringExtra("user_phone");


            Button add_button = (Button) findViewById(R.id.main_button1);

            add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_details = new Intent(v.getContext(), Details.class);
                    startActivity(intent_details);
                }
            });

            final Button del_button = (Button) findViewById(R.id.main_button2);
            del_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences del_phones = getSharedPreferences("user_phone", MODE_PRIVATE);
                    SharedPreferences.Editor phone_editor = del_phones.edit();

                    SharedPreferences del_relation = getSharedPreferences("user_relation", MODE_PRIVATE);
                    SharedPreferences.Editor del_relation_editor = del_relation.edit();
                    if(delete_list.size() > 0) {
                        for(int j = 0; j < delete_list.size(); j++) {
                            for(int i = 0; i < myarray.size(); i++) {
                                if(myarray.get(i).get("user_name").toString().equals(delete_list.get(j).toString()) ) {
                                    String cur_delete = myarray.get(i).get("user_name").toString();
                                    //delete shared preference and arraylist
                                    del_phones.edit().remove(myarray.get(i).get("user_name").toString()).commit();
                                    del_relation_editor.remove(cur_delete).commit();
                                    myarray.remove(i);
                                }
                            }
                        }
                    }

                    //delete relation
                    for(int i = 0; i < delete_list.size(); i++) {
                        SharedPreferences names = getSharedPreferences("user_relation", MODE_PRIVATE);
                        Map<String, ?> allEntries = names.getAll();
                        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                            String cur_user_neme = entry.getKey();
                            String cur_user_imformation = entry.getValue().toString();
                            if(cur_user_imformation.isEmpty()) continue;
                            String[] s = cur_user_imformation.split(",");
                            List<String> need_delete = Arrays.asList(s);
                            List<String> new_item = new ArrayList<String>();
                            for(String tmp : need_delete) {
                                if(tmp.equals(delete_list.get(i).toString())) {
                                    continue;
                                }
                                else {
                                    new_item.add(tmp);
                                }
                            }

                            StringBuilder result=new StringBuilder();
                            boolean flag=false;
                            for (String string : new_item) {
                                if (flag) {
                                    result.append(",");
                                }else {
                                    flag=true;
                                }
                                result.append(string);
                            }
                            String reconstructor = result.toString();
                            SharedPreferences.Editor relation_editor = names.edit();
                            relation_editor.remove(cur_user_neme.toString()).commit();
                            relation_editor.putString(cur_user_neme, reconstructor);
                            relation_editor.apply();

                        }
                    }
                    adapter.notifyDataSetChanged();
                    delete_list.clear();

                }
            });

            main_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent_profile = new Intent(view.getContext(), Profile.class);
                    intent_profile.putExtra("user_name", myarray.get(position).get("user_name").toString());
                    startActivity(intent_profile);
                }
            });
        }


    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String name = intent.getStringExtra("user_name");
        myarray.clear();
        //read user information
        SharedPreferences names = getSharedPreferences("user_phone", MODE_PRIVATE);
        Map<String, ?> allEntries = names.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String user_name = entry.getKey();
            String user_information = entry.getValue().toString();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("user_name", user_name);
            map.put("user_info", user_information);
            myarray.add(map);
        }

        adapter = new Mainlist_adpter(this, myarray);

        main_listview.setAdapter(adapter);

    }

//    @Override
//    public void onConfigurationChanged(Configuration config) {
//        super.onConfigurationChanged(config);
//        if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
//
//        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();


    }



    class Mainlist_adpter extends ArrayAdapter {

        private Context context;
        private LayoutInflater inflater = null;
        private ArrayList<Map<String, String>> myarray;

        public Mainlist_adpter(Context context, ArrayList<Map<String, String>> data) {
            super(context, R.layout.main_list, data);
            this.context = context;
            this.myarray = data;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_list, parent, false);
            TextView name = (TextView) (convertView.findViewById(R.id.Main_inner_layout)).findViewById(R.id.Main_list_item);
            CheckBox cb = (CheckBox) (convertView.findViewById(R.id.Main_inner_layout)).findViewById(R.id.Main_list_checkbox);
            name.setText(myarray.get(position).get("user_name"));

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                            delete_list.add(myarray.get(position).get("user_name"));
                    }
                }
            });

            return convertView;
        }
    }
}