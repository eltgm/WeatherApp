package com.example.eltgm.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Launcher extends AppCompatActivity {

    private EditText editText;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_layout);

        editText = (EditText)findViewById(R.id.editText);
        editText.setVisibility(View.VISIBLE);
        mContext = getApplicationContext();
    }

    //"http://api.openweathermap.org/data/2.5/forecast?q=Moscow&appid=295f7bee433d8cf26fd64d9ab085726b"
    public void enterWeather(View view) {
        Intent intent = new Intent(Launcher.this,MainActivity.class);
        String  i = editText.getText().toString();
        int k = 0;
        if (!(editText.getText().toString()).equals("")) {
            String url = "http://api.openweathermap.org/data/2.5/forecast?q=" + editText.getText().toString().toLowerCase()
                    + "&appid=295f7bee433d8cf26fd64d9ab085726b";
            intent.putExtra("url", url);
            intent.putExtra("cityName", editText.getText().toString().toLowerCase());
            startActivity(intent);
        } else{
            Toast.makeText(getApplicationContext(), "Введите название города!", Toast.LENGTH_LONG).show();
        }
    } //входим в приложение с вводом города вручную
/*
    //api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&appid=295f7bee433d8cf26fd64d9ab085726b
    public void findPosition(View view) {
        SmartLocation.with(this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                //url = " ";
                Intent intent = new Intent(Launcher.this,MainActivity.class);
                url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLatitude() +"&lon="+ location.getLongitude() +
                        "&appid=295f7bee433d8cf26fd64d9ab085726b";
                intent.putExtra("url", url);
                Log.e("button_pressed","Кнопка была нажата");
                startActivity(intent);
            }
        });
        if(!SmartLocation.with(this).location().state().isAnyProviderAvailable()) {
            Toast.makeText(getApplicationContext(),"Geolocation is unavailable",Toast.LENGTH_LONG).show();
        }
    } //входим в приложение с геолокацией*/
}//класс, запускающий лаунчер
