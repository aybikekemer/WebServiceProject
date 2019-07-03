package com.vizeapp.projectwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    ListView listView;


    @Override
    //use listview to show the values which received from the database
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = findViewById(R.id.listView);

        ArrayList<String> list = new ArrayList<String>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(arrayAdapter);

        try {

            MainActivity.database = this.openOrCreateDatabase("Currency", MODE_PRIVATE, null);
            MainActivity.database.execSQL("CREATE TABLE IF NOT EXISTS currency (date VARCHAR, value INT(5))");

            Cursor cursor = MainActivity.database.rawQuery("SELECT * FROM currency",null);

            int nameIx = cursor.getColumnIndex("date");
            int valueIx = cursor.getColumnIndex("value");

            cursor.moveToFirst();

            while (cursor != null) {

                list.add(cursor.getString(nameIx) + " " + cursor.getString(valueIx));
                cursor.moveToNext();

                arrayAdapter.notifyDataSetChanged();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}

