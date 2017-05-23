package com.example.eltgm.weatherapp;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eltgm.weatherapp.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    NotificationManager notificationManager;
    DbHelper dbHelper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    } //создание меню

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.history:
                Intent intent = new Intent(this, History.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Toast.makeText(this,"В разработке...",Toast.LENGTH_LONG).show();
                return true;
            case R.id.about:
                Toast.makeText(this,"WeatherApp" + "\n" + "2017",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(getApplicationContext());

        final String url = getIntent().getExtras().getString("url");
        final String cityName = getIntent().getExtras().getString("cityName");

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, //конфигурация сетевого запроса
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final WeatherDay[] days = getWeather(response);
                        insertToDb(days);
                        dayTempSet(days[0]);
                        WeatherDay[] daysFour = Arrays.copyOfRange(days,1,days.length); // передаем только 4 дня,не включая сегодняшний
                        final RecyclerView rvMain = (RecyclerView) findViewById(R.id.rvMain);
                        // Create adapter passing in the sample user data

                        final WeatherAdapter adapter = new WeatherAdapter(MainActivity.this, daysFour);
                        // Attach the adapter to the recyclerview to populate items
                        rvMain.setAdapter(adapter);
                        // Set layout manager to position the items
                        rvMain.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                    }
                },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                WeatherDay[] days = dbHelper.getCityWeathers(cityName.toLowerCase(),dbHelper);
                if (days != null) {
                    dayTempSet(dbHelper.getCityWeather(cityName.toLowerCase(), dbHelper, -1));

                    final RecyclerView rvMain = (RecyclerView) findViewById(R.id.rvMain);
                    final WeatherAdapter adapter = new WeatherAdapter(MainActivity.this, days);
                    rvMain.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvMain.setAdapter(adapter);
                }else {
                    Toast.makeText(MainActivity.this, "Не могу найти такой город!", Toast.LENGTH_LONG).show();
                }
            }
        });
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest); //отправка сетевого запроса

    }

    public void insertToDb(WeatherDay[] days) {
        String cityString = (days[0].getDay())[0].getCityName();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("cities", null, null, null, null, null, null);

        boolean hasCity = false;
        long cityId = Long.parseLong((days[0].getDay())[0].getId());
        if(c.moveToFirst())
        {
            int nameColIndex = c.getColumnIndex("name");
            int idColIndex = c.getColumnIndex("city_id");

            do {
                if(c.getString(nameColIndex).toLowerCase().equals(cityString.toLowerCase()))
                {
                    hasCity = true;
                    cityId = c.getInt(idColIndex);
                    c.close();
                    break;
                }

            } while (c.moveToNext());
        } else {
            c.close();
        }

        String ID = (days[0].getDay())[0].getId();

        if(!hasCity) {
            int k = 1;
            JSONObject tempObj = new JSONObject();
            JSONObject humObj = new JSONObject();
            JSONObject presObj = new JSONObject();
            JSONObject windObj = new JSONObject();
            JSONObject descrObj = new JSONObject();

            ContentValues cityCv = new ContentValues();
            cityCv.put("name", cityString.toLowerCase());
            cityCv.put("city_id",cityId);
            db.insert("cities", null, cityCv);

            hasCity = true;
            ContentValues tempValues = new ContentValues();
            ContentValues windValues = new ContentValues();
            ContentValues presValues = new ContentValues();
            ContentValues humValues = new ContentValues();
            ContentValues descrValues = new ContentValues();

            for (WeatherDay day : days) {
                long[] temps = new long[day.getDay().length];
                double[] pres = new double[day.getDay().length];
                double[] wind = new double[day.getDay().length];
                long[] hum = new long[day.getDay().length];
                String[] descr = new String[day.getDay().length];
                long[] sec = new long[day.getDay().length];

                for (int j = 0; j < temps.length; j++) {
                    temps[j] = (day.getDay())[j].getTemp();
                    pres[j] = (day.getDay())[j].getPressure();
                    wind[j] = (day.getDay())[j].getWindSpeed();
                    hum[j] = (day.getDay())[j].getHumidity();
                    descr[j] = (day.getDay())[j].getDescription();
                    sec[j] = (day.getDay())[j].getUnix();
                }

                for (int j = 0; j < temps.length; j++) {
                    SimpleDateFormat parseFormat = new SimpleDateFormat("H", Locale.ROOT);
                    Date date = new Date((sec[j] - 10800) * 1000);
                    String newDate = parseFormat.format(date);

                    tempObj.put(newDate + "temp", temps[j]);
                    humObj.put(newDate + "hum", hum[j]);
                    presObj.put(newDate + "pres", pres[j]);
                    windObj.put(newDate + "wind", wind[j]);
                    descrObj.put(newDate + "descr", descr[j]);
                }

                tempObj.put("day", sec[0]);
                humObj.put("day", sec[0]);
                presObj.put("day", sec[0]);
                windObj.put("day", sec[0]);
                descrObj.put("day", sec[0]);

                String jsonTemp = tempObj.toString();
                tempValues.put("temp" + (k), jsonTemp);

                String jsonWind = windObj.toString();
                windValues.put("wind" + (k), jsonWind);

                String jsonPres = presObj.toString();
                presValues.put("pres" + (k), jsonPres);

                String jsonHum = humObj.toString();
                humValues.put("hum" + (k), jsonHum);

                String jsonDescr = descrObj.toString();
                descrValues.put("descr" + (k), jsonDescr);
                k++;
            }

            tempValues.put("city_id",Long.valueOf(ID));
            humValues.put("city_id",Long.valueOf(ID));
            presValues.put("city_id",Long.valueOf(ID));
            windValues.put("city_id",Long.valueOf(ID));
            descrValues.put("city_id",Long.valueOf(ID));

            db.insert("temp", null, tempValues);
            db.insert("hum",null,humValues);
            db.insert("pres",null,presValues);
            db.insert("wind",null,windValues);
            db.insert("descr",null,descrValues);
        } else {
            int k = 1;
            JSONObject tempObj = new JSONObject();
            JSONObject humObj = new JSONObject();
            JSONObject presObj = new JSONObject();
            JSONObject windObj = new JSONObject();
            JSONObject descrObj = new JSONObject();

            ContentValues tempValues = new ContentValues();
            ContentValues windValues = new ContentValues();
            ContentValues presValues = new ContentValues();
            ContentValues humValues = new ContentValues();
            ContentValues descrValues = new ContentValues();

            for (WeatherDay day : days) {

                Weather[] weathers = day.getDay();
                long[] temps = new long[day.getDay().length];
                double[] pres = new double[day.getDay().length];
                double[] wind = new double[day.getDay().length];
                long[] hum = new long[day.getDay().length];
                String[] descr = new String[day.getDay().length];
                long[] sec = new long[day.getDay().length];

                for (int j = 0; j < temps.length; j++) {
                    temps[j] = (day.getDay())[j].getTemp();
                    pres[j] = (day.getDay())[j].getPressure();
                    wind[j] = (day.getDay())[j].getWindSpeed();
                    hum[j] = (day.getDay())[j].getHumidity();
                    descr[j] = (day.getDay())[j].getDescription();
                    sec[j] = (day.getDay())[j].getUnix();
                }

                for (Weather weather : weathers) {
                    SimpleDateFormat parseFormat = new SimpleDateFormat("H", Locale.ROOT);
                    Date date = new Date((weather.getUnix() - 10800) * 1000);
                    String newDate = parseFormat.format(date);

                    tempObj.put(newDate + "temp", weather.getTemp());
                    humObj.put(newDate + "hum", weather.getHumidity());
                    presObj.put(newDate + "pres", weather.getPressure());
                    windObj.put(newDate + "wind", weather.getWindSpeed());
                    descrObj.put(newDate + "descr", weather.getDescription());
                }
                tempObj.put("day", sec[0]);
                humObj.put("day", sec[0]);
                presObj.put("day", sec[0]);
                windObj.put("day", sec[0]);
                descrObj.put("day", sec[0]);

                String jsonTemp = tempObj.toString();
                tempValues.put("temp" + (k), jsonTemp);

                String jsonWind = windObj.toString();
                windValues.put("wind" + (k), jsonWind);

                String jsonPres = presObj.toString();
                presValues.put("pres" + (k), jsonPres);

                String jsonHum = humObj.toString();
                humValues.put("hum" + (k), jsonHum);

                String jsonDescr = descrObj.toString();
                descrValues.put("descr" + (k), jsonDescr);

                k++;
            }

            tempValues.put("city_id",Long.valueOf(ID));
            humValues.put("city_id",Long.valueOf(ID));
            presValues.put("city_id",Long.valueOf(ID));
            windValues.put("city_id",Long.valueOf(ID));
            descrValues.put("city_id",Long.valueOf(ID));

            db.update("temp", tempValues, "city_id = " + cityId,null);
            db.update("hum", humValues, "city_id = " + cityId,null);
            db.update("pres", presValues, "city_id = " + cityId,null);
            db.update("wind", windValues, "city_id = " + cityId,null);
            db.update("descr", descrValues, "city_id = " + cityId,null);

        }
    } //создание/обновление записи в бд с городом

    public WeatherDay[] getWeather(String response){
        JSONParser parser = new JSONParser();
        Object JSONobj = null;
        Weather[] tempDay;
        int start;

        try {
            JSONobj = parser.parse(response);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject cityAll = (JSONObject) JSONobj;
        JSONObject cityName = (JSONObject) cityAll.get("city");
        JSONArray cityWeatherList = (JSONArray) cityAll.get("list");

        Weather[] tempBuff = new Weather[cityWeatherList.size()];

        for(int i = 0; i < tempBuff.length; i++){
            JSONObject cityDayWeather = (JSONObject) cityWeatherList.get(i);
            JSONObject cityDayTempAll = (JSONObject) cityDayWeather.get("main");
            JSONObject descriptionObject = (JSONObject) ((JSONArray) cityDayWeather.get("weather")).get(0);
            String nowTempK =  cityDayTempAll.get("temp").toString();
            JSONObject innerWind = (JSONObject) cityDayWeather.get("wind");

            Weather temp = new Weather(Double.valueOf(nowTempK) - 273.15,cityDayWeather.get("dt_txt").toString(),descriptionObject.get("description").toString(),
                    Double.valueOf(innerWind.get("speed").toString()),Integer.valueOf(cityDayTempAll.get("humidity").toString()),
                    Integer.valueOf(cityDayWeather.get("dt").toString()), Double.valueOf(cityDayTempAll.get("pressure").toString()),String.valueOf(((JSONObject)cityAll.get("city")).get("id"))
                    ,String.valueOf(cityName.get("name")));
            tempBuff[i] = temp;
        }

        for(start = 0; start < tempBuff.length; start++){
            if(tempBuff[start].date.contains("00:00:00")){
                break;
            }
        }
        tempDay = Arrays.copyOfRange(tempBuff,0,tempBuff.length);

        int count = 0;
        int oldCount = count;
        WeatherDay[] weatherDays = new WeatherDay[5];

        for(int i = 0; i < 5; i++)
            for(; count <= tempDay.length; count++){
                if(count == tempDay.length){
                    Weather[] tempDays = Arrays.copyOfRange(tempDay, oldCount, count);
                    oldCount = count;
                    weatherDays[i] = new WeatherDay(tempDays);
                }
                else {
                    if (tempDay[count].date.contains("00:00:00") && count != oldCount) {
                        Weather[] tempDays = Arrays.copyOfRange(tempDay, oldCount, count);
                        oldCount = count;
                        weatherDays[i] = new WeatherDay(tempDays);
                        break;
                    }
                }
            }
        return weatherDays;
    }//создаем массив погод на 5 дня

    @SuppressLint("SimpleDateFormat")
    private void dayTempSet(WeatherDay days){
        final TextView tvDay = (TextView)findViewById(R.id.tvDay);
        final TextView tvDayTemp = (TextView)findViewById(R.id.tvDayTemp);
        final TextView tvWind = (TextView)findViewById(R.id.tvWind);
        final TextView tvOsadki = (TextView)findViewById(R.id.tvOsadki);
        final TextView tvCityName = (TextView)findViewById(R.id.tvEnter);

        final Date date = new Date();
        final Date date1 = new Date((days.getDay()[0].getUnix() - 3*60*60) * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy H:mm");
        SimpleDateFormat mask = new SimpleDateFormat("dd");

        int timeZone = 0;
        if((Integer.valueOf(mask.format(date)) - Integer.valueOf(mask.format(date1))) != 0) //new Date()
            timeZone = (new Date().getHours())/3 - 1;
        tvDay.setText(dateFormat.format(date));
        tvDayTemp.setText(String.valueOf((days.getDay())[timeZone].getTemp()));
        tvOsadki.setText((days.getDay())[timeZone].getDescription());
        tvWind.setText("wind speed: " + days.getDay()[timeZone].getWindSpeed() + "m/s");
        String retCityName = (days.getDay())[timeZone].getCityName().substring(0,1).toUpperCase() +
                (days.getDay())[timeZone].getCityName().substring(1,(days.getDay())[timeZone].getCityName().length());
        tvCityName.setText(retCityName);

        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(android.R.drawable.ic_media_next)
                .setContentTitle(retCityName)
                .setContentText(String.valueOf((days.getDay())[timeZone].getTemp())); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(10, notification);
    }//заполняем погоду для сегодняшнего дня

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(10);
    } //при уничтожении активности
}

