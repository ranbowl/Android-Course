package com.example.xinyu.assignment_1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private String fileName = "user.txt";
    private ArrayList<Map<String, String>> myarray = new ArrayList<Map<String, String>>();
    private ListAdapter adapter;
    private ListView my_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_list = (ListView) findViewById(R.id.mylist) ;

        Button bt = (Button)findViewById(R.id.task_button);



        bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                EditText et_1 = (EditText) findViewById(R.id.input_1);
                String input_1 = et_1.getText().toString();
                EditText et_2 = (EditText) findViewById(R.id.iput_2);
                String input_2 = et_2.getText().toString();
                // ArrayList<HashMap<String,String>> myarray = new ArrayList<HashMap<String, String>>();
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("ItemTitle", input_1);
                map.put("ItemText", input_2);
                adapter.add(map);

                //save to file
                String tmp = input_1 + " " + input_2;
                try {
                    FileOutputStream outputStream = openFileOutput(fileName, MainActivity.MODE_APPEND);
                    byte []newLine="\r\n".getBytes();
                    outputStream.write(tmp.getBytes());
                    outputStream.write(newLine);
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected   void onStart() {
        super.onStart();
        try {
            FileInputStream inputStream = openFileInput(fileName);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String tmp = scanner.nextLine().toString();
                String[] s = tmp.split(" ");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", s[0]);
                map.put("ItemText", s[1]);
                myarray.add(map);
            }
            scanner.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new ListAdapter(this, myarray);
        my_list.setAdapter(adapter);

        my_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> list, View row, int position, long rowID) {

                        Object removeItem = adapter.getItem(position);
                        adapter.remove(removeItem);
                        return true;
                    }
                });


    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            FileOutputStream outputStream = openFileOutput(fileName, MainActivity.MODE_PRIVATE);
            byte []newLine="\r\n".getBytes();
            for(Map<String, String> oneline : myarray) {
                outputStream.write((oneline.get("ItemTitle").toString() + " " + oneline.get("ItemText").toString()).toString().getBytes());
                outputStream.write(newLine);
            }
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
