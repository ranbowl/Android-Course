package com.example.xinyu.assignment2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.view.Display;
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
import java.util.Scanner;


public class Fragment_as2 extends Fragment {
    private Activity myActivity;
    private ArrayList<Contacts> mContactsList;
    private Mainlist_adpter adapter;
    private ArrayList<Map<String, String>> myarray = new ArrayList<Map<String, String>>();
    private ListView main_listview;
    public ArrayList delete_list = new ArrayList();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment1,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        Button deleteButton;
        deleteButton = (Button)myActivity.findViewById(R.id.main_button2);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences del_phones = myActivity.getSharedPreferences("user_phone", Context.MODE_PRIVATE);
                SharedPreferences del_relation = myActivity.getSharedPreferences("user_relation", Context.MODE_PRIVATE);

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
                    SharedPreferences names = myActivity.getSharedPreferences("user_relation", Context.MODE_PRIVATE);
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
        Button addButton;
        addButton = (Button)myActivity.findViewById(R.id.main_button1);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_details = new Intent(v.getContext(), Details.class);
                startActivity(intent_details);
            }
        });
        Intent intent = myActivity.getIntent();
        String name = intent.getStringExtra("user_name");
        myarray.clear();
        //read user information
        SharedPreferences names = myActivity.getSharedPreferences("user_phone", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = names.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String user_name = entry.getKey();
            String user_information = entry.getValue().toString();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("user_name", user_name);
            map.put("user_info", user_information);
            myarray.add(map);
        }

        adapter = new Mainlist_adpter(getContext(), myarray);

        main_listview.setAdapter(adapter);
        main_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_profile = new Intent(view.getContext(), Profile.class);
                intent_profile.putExtra("user_name", myarray.get(position).get("user_name").toString());
                startActivity(intent_profile);
            }
        });
    }
    public void updateFragment(String data){

        String read_data=data;

        if(read_data!="") {
            mContactsList.clear();
            Scanner scanner = new Scanner(read_data);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] pieces = line.split("\t");
                if (pieces.length == 2) {
                    mContactsList.add(new Contacts(pieces[0], pieces[1], ""));
                } else{
                    mContactsList.add(new Contacts(pieces[0], pieces[1], pieces[2]));
                }
            }
            scanner.close();
        }
        adapter.notifyDataSetChanged();
        //saveData();
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
    class Contacts {
        private String name;
        private String number;
        private String relationship;
        private boolean selected = false;

        public Contacts(String name, String number, String relationship) {
            this.name = name;
            this.number = number;
            this.relationship = relationship;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {return number;}

        public void setNumber(String number) {this.number = number;}

        public String getRelationship() {return relationship;}

        public void setRelationship(String relationship) {this.relationship = relationship;}

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
