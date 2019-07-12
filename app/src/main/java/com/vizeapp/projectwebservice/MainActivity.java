package com.vizeapp.projectwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtEur = findViewById(R.id.txtEur);
        txtEur.setText("1 €");
        txtTry = findViewById(R.id.txtTry);
        txtDate = findViewById(R.id.txtDate);


        //want to save the date for each day to remember in future.
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        txtDate.setText(formattedDate);

        //to access web service
        try {
            String url = "http://data.fixer.io/api/latest?access_key=a4b5ec9b30a28001fb5b5b88566fcfb6";
            downloadData.execute(url);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //database part
    //value -->  unit provision for Turkish currency
    //date --> date of using app
    public void save(View view) {

        try {
            String today = txtDate.getText().toString();
            String selected = txtTry.getText().toString();

            SQLiteDatabase database = this.openOrCreateDatabase("Currency",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS currency (date VARCHAR, value INT(5))");

            String sqlString = "INSERT INTO currency (date,value) VALUES (?,?)";
            SQLiteStatement statement = database.compileStatement(sqlString);
            statement.bindString(1,today);
            statement.bindString(2,selected);
            statement.execute();

            Cursor cursor = database.rawQuery("SELECT * FROM currency",null);
            int dateIx = cursor.getColumnIndex("date");
            int valueIx = cursor.getColumnIndex("value");

            cursor.moveToFirst();
            while(cursor != null){
                System.out.println(cursor.getString(dateIx) + " " + cursor.getString(valueIx));
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //this part about to receive data from web service.
    //send requests to receive data by giving url value.
    //get all data (char form)
    private class DownloadData extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data > 0) {
                    char character = (char) data;
                    result += character;

                    data = inputStreamReader.read();
                }



            }catch (Exception e){
                return null;
            }
            return result;

        }

        // scan with the hint to access the value
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                String rates = jsonObject.getString("rates");

                JSONObject jsonObject1 = new JSONObject(rates);
                String turkish = jsonObject1.getString("TRY"); // TRY is hint
                txtTry.setText(turkish);

            }catch (Exception e){
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }


    }

    // 'show' method to access second page
    public void show(View view){
        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
        startActivity(intent);
    }

}
