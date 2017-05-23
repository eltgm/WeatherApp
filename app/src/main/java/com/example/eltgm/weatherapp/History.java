package com.example.eltgm.weatherapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class History extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = database.query("cities", new String[]{"name"}, null, null, null, null, null);

        String[] cityName = new String[c.getCount()];
        int i = 0;
        if(c.moveToFirst()){
            do{
                cityName[i] = c.getString(c.getColumnIndex("name")).substring(0,1).toUpperCase() +
                        c.getString(c.getColumnIndex("name")).substring(1,c.getString(c.getColumnIndex("name")).length());
                i++;
            }while (c.moveToNext());
        }else
            c.close();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvHistory);

        HistoryAdapter historyAdapter = new HistoryAdapter(cityName,this);

        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
