package com.example.xinyu.assignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Details extends AppCompatActivity {
    private Details_adpter details_adapter;
    private ArrayList<Map<String, String>> Detialsarray = new ArrayList<Map<String, String>>();
    private ListView Details_listview;
    public List<String > rel_list = new ArrayList<String>();
    int check_total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        check_total = 0;
        Details_listview = (ListView) findViewById(R.id.Details_list);
        //Details_listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button bt = (Button) findViewById(R.id.Details_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_1 = (EditText) findViewById(R.id.Details_input_name);
                String input_1 = et_1.getText().toString();
                EditText et_2 = (EditText) findViewById(R.id.Detais_input_phone);
                String input_2 = et_2.getText().toString();

                StringBuilder result=new StringBuilder();
                boolean flag=false;
                for (String string : rel_list) {
                    if (flag) {
                        result.append(",");
                    }else {
                        flag=true;
                    }
                    result.append(string);
                }
                String tmp = result.toString();

                SharedPreferences add_phones = getSharedPreferences("user_phone", MODE_PRIVATE);
                SharedPreferences.Editor phone_editor = add_phones.edit();
                phone_editor.putString(input_1, input_2);
                phone_editor.apply();

                SharedPreferences add_rel = getSharedPreferences("user_relation", MODE_PRIVATE);
                SharedPreferences.Editor relation_editor = add_rel.edit();
                relation_editor.putString(input_1, tmp);
                relation_editor.apply();


                //change original relation

                String ori_information = new String();
                SharedPreferences del_relation = getSharedPreferences("user_relation", MODE_PRIVATE);
                SharedPreferences.Editor del_rel_editor = del_relation.edit();
                for (String del_string : rel_list) {
                    //read original relation
                    SharedPreferences names = getSharedPreferences("user_relation", MODE_PRIVATE);
                    Map<String, ?> allEntries = names.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        String user_name = entry.getKey();
                        if(user_name.toString().equals(del_string.toString())) {
                            ori_information = entry.getValue().toString();
                            break;
                        }
                    }

                    //Add relation
                    if(ori_information.isEmpty()) {
                        ori_information = input_1.toString();
                    }
                    else {
                        ori_information = (ori_information + "," + input_1).toString();
                    }

                    //delete
                    del_rel_editor.remove(del_string).commit();
                    // write
                    relation_editor.putString(del_string, ori_information).commit();

                }





                Intent intent_main = new Intent(v.getContext(), MainActivity.class);
                intent_main.putExtra("user_name", input_1);
//                intent_main.putExtra("user_phone", input_2);
                startActivity(intent_main);
            }
        });

        Details_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_profile = new Intent(view.getContext(), Profile.class);
                intent_profile.putExtra("user_name", Detialsarray.get(position).get("user_name").toString());
                startActivity(intent_profile);
            }
        });

    }

    @Override
    protected   void onStart() {
        super.onStart();
        //read user information
        SharedPreferences names = getSharedPreferences("user_phone", MODE_PRIVATE);
        Map<String, ?> allEntries = names.getAll();
        Detialsarray.clear();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String user_name = entry.getKey();
            String user_information = entry.getValue().toString();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("user_name", user_name);
            map.put("user_info", user_information);
            Detialsarray.add(map);
        }

        details_adapter = new Details_adpter(this, Detialsarray);
        Details_listview.setAdapter(details_adapter);


    }

    class Details_adpter extends ArrayAdapter {

        private Context context;
        private LayoutInflater inflater = null;
        private ArrayList<Map<String, String>> myarray;
        public ArrayList<Boolean> isSelected;

        public Details_adpter(Context context, ArrayList<Map<String, String>> data) {
            super(context, R.layout.main_list, data);
            this.context = context;
            this.myarray = data;
            init();
        }


        public void init() {
            isSelected = new ArrayList<Boolean>();
            for (int i = 0; i < myarray.size(); i++) {
                isSelected.add(false);
            }
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_list, parent, false);
            TextView name = (TextView) (convertView.findViewById(R.id.Main_inner_layout)).findViewById(R.id.Main_list_item);
            final CheckBox cb = (CheckBox) (convertView.findViewById(R.id.Main_inner_layout)).findViewById(R.id.Main_list_checkbox);
            name.setText(myarray.get(position).get("user_name"));
            cb.setChecked(isSelected.get(position));


            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        if(!rel_list.contains(myarray.get(position).get("user_name").toString())) {
                            rel_list.add(myarray.get(position).get("user_name").toString());
                        }
                        isSelected.set(position,cb.isChecked());
                        Map<String, String> removeLine = myarray.get(position);
                        myarray.remove(position);
                        myarray.add(0,removeLine);

                        boolean removeLine2 = isSelected.get(position);
                        isSelected.remove(position);
                        isSelected.add(0, removeLine2);


                        notifyDataSetChanged();
                    }
                }
            });

            return convertView;



        }
    }


}
