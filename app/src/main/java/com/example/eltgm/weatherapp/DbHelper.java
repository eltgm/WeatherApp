package com.example.eltgm.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    Context context;

    public DbHelper(Context context){
        super(context,"myDb",null,1);
        this.context = context;
    }//создание базы данных

    public WeatherDay getCityWeather(String cityName, DbHelper helper, int day){//возвращает один день с погодой
        String temp1Json;
        String hum1Json;
        String pres1Json;
        String wind1Json;
        String descr1Json;
        String dayJson;

        SQLiteDatabase db = helper.getReadableDatabase();

        // SELECT * FROM temp WHERE

        Cursor c = db.query("cities", null, "name='" + cityName + "'", null, null, null, null); //3параметр cityid

        if(c.moveToFirst()) {
            int cityIdColIndex = c.getColumnIndex("city_id");

            Cursor temps = db.query("temp", null, "city_id=" + c.getInt(c.getColumnIndex("city_id")) + "", null, null, null, null);
            Cursor hum = db.query("hum", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);
            Cursor pres = db.query("pres", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);
            Cursor wind = db.query("wind", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);
            Cursor descr = db.query("descr", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);

            if (temps.moveToFirst() && hum.moveToFirst() && pres.moveToFirst() && wind.moveToFirst()
                    && descr.moveToFirst()) {
                dayJson = temps.getString(temps.getColumnIndex("temp1"));
                JSONParser parser = new JSONParser();
                Object JSONobj = null;

                try {
                    JSONobj = parser.parse(dayJson);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                JSONObject tempObj = (JSONObject) JSONobj;
                final Date date = new Date();

                SimpleDateFormat mask = new SimpleDateFormat("dd", Locale.ROOT);

                long dayValue = ((long) tempObj.get("day") - 3 * 60 * 60) * 1000;
                final Date date1 = new Date(dayValue);
                long past = 1;

                if (day == -1) {
                    if ((Integer.valueOf(mask.format(date)) - Integer.valueOf(mask.format(date1))) != 0) //{
                        past = Integer.valueOf(mask.format(date)) - Integer.valueOf(mask.format(date1)) + 1;

                    temp1Json = temps.getString(temps.getColumnIndex("temp" + past));
                    hum1Json = hum.getString(hum.getColumnIndex("hum" + past));
                    pres1Json = pres.getString(pres.getColumnIndex("pres" + past));
                    wind1Json = wind.getString(wind.getColumnIndex("wind" + past));
                    descr1Json = descr.getString(descr.getColumnIndex("descr" + past));
                    return new WeatherDay(temp1Json, hum1Json, wind1Json, pres1Json, descr1Json, String.valueOf(c.getString(c.getColumnIndex("name"))));
                } else {
                    temp1Json = temps.getString(temps.getColumnIndex("temp" + day));
                    hum1Json = hum.getString(hum.getColumnIndex("hum" + day));
                    pres1Json = pres.getString(pres.getColumnIndex("pres" + day));
                    wind1Json = wind.getString(wind.getColumnIndex("wind" + day));
                    descr1Json = descr.getString(descr.getColumnIndex("descr" + day));
                    String retCityName = c.getString(c.getColumnIndex("name"));
                    retCityName = retCityName.substring(0,1).toUpperCase() + retCityName.substring(1, retCityName.length());
                    return new WeatherDay(temp1Json, hum1Json, wind1Json, pres1Json, descr1Json, retCityName);
                }
            }
        } else
            c.close();
        return null;
    }

    public WeatherDay[] getCityWeathers(String cityName, DbHelper helper){
        String temp1Json;
        String hum1Json;
        String pres1Json;
        String wind1Json;
        String descr1Json;
        String dayJson;

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query("cities", null, "name='" + cityName + "'", null, null, null, null);

        if(c.moveToFirst()) {
            int cityIdColIndex = c.getColumnIndex("city_id");

            Cursor temps = db.query("temp", null, "city_id=" + c.getInt(c.getColumnIndex("city_id")) + "", null, null, null, null);
            Cursor hum = db.query("hum", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);
            Cursor pres = db.query("pres", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);
            Cursor wind = db.query("wind", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);
            Cursor descr = db.query("descr", null, "city_id=" + c.getInt(cityIdColIndex) + "", null, null, null, null);

            if (temps.moveToFirst() && hum.moveToFirst() && pres.moveToFirst() && wind.moveToFirst()
                    && descr.moveToFirst()) {

                dayJson = temps.getString(temps.getColumnIndex("temp5"));
                JSONParser pars = new JSONParser();
                Object obj = null;

                try {
                    obj = pars.parse(dayJson);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                JSONObject tempOb = (JSONObject) obj;
                final Date d = new Date();

                SimpleDateFormat mask1 = new SimpleDateFormat("dd", Locale.ROOT);

                long day = ((long) tempOb.get("day") - 3 * 60 * 60) * 1000;
                final Date d1 = new Date(day);

                WeatherDay[] weathers = new WeatherDay[(Integer.valueOf(mask1.format(d1)) - Integer.valueOf(mask1.format(d)))];
                int count = 0;

                for (int past = 2; past <= 5; past++) {

                    dayJson = temps.getString(temps.getColumnIndex("temp" + past));
                    JSONParser parser = new JSONParser();
                    Object JSONobj = null;

                    try {
                        JSONobj = parser.parse(dayJson);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    JSONObject tempObj = (JSONObject) JSONobj;
                    final Date date = new Date();

                    SimpleDateFormat mask = new SimpleDateFormat("dd", Locale.ROOT);

                    long dayValue = ((long) tempObj.get("day") - 3 * 60 * 60) * 1000;
                    final Date date1 = new Date(dayValue);

                    if ((Integer.valueOf(mask.format(date1)) - Integer.valueOf(mask.format(date))) > 0) {
                        temp1Json = temps.getString(temps.getColumnIndex("temp" + past));
                        hum1Json = hum.getString(hum.getColumnIndex("hum" + past));
                        pres1Json = pres.getString(pres.getColumnIndex("pres" + past));
                        wind1Json = wind.getString(wind.getColumnIndex("wind" + past));
                        descr1Json = descr.getString(descr.getColumnIndex("descr" + past));
                        weathers[count] = new WeatherDay(temp1Json, hum1Json, wind1Json, pres1Json, descr1Json, String.valueOf(c.getString(c.getColumnIndex("name"))));
                        count++;
                    } else {
                        continue;
                    }
                }
                return weathers;
            } else {
                Toast.makeText(context,"Не могу сформировать запрос к API или найти запись в базе данных!",
                        Toast.LENGTH_LONG).show();
            }

        } else
            c.close();
        return null;
    } //метод, создающий массив дней с погодой

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table cities (" +
                "_id integer primary key autoincrement," +
                "city_id integer,"+
                "name text);");

        db.execSQL("create table temp (" +
                "_id integer primary key autoincrement," +
                "city_id integer," +
                "temp1 text," +
                "temp2 text," +
                "temp3 text," +
                "temp4 text," +
                "temp5 text);");

        db.execSQL("create table hum (" +
                "_id integer primary key autoincrement," +
                "city_id integer," +
                "hum1 text," +
                "hum2 text," +
                "hum3 text," +
                "hum4 text," +
                "hum5 text);");

        db.execSQL("create table pres (" +
                "_id integer primary key autoincrement," +
                "city_id integer," +
                "pres1 text," +
                "pres2 text," +
                "pres3 text," +
                "pres4 text," +
                "pres5 text);");

        db.execSQL("create table wind (" +
                "_id integer primary key autoincrement," +
                "city_id integer," +
                "wind1 text," +
                "wind2 text," +
                "wind3 text," +
                "wind4 text," +
                "wind5 text);");

        db.execSQL("create table descr (" +
                "_id integer primary key autoincrement," +
                "city_id integer," +
                "descr1 text," +
                "descr2 text," +
                "descr3 text," +
                "descr4 text," +
                "descr5 text);");
    }//создание таблиц

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }//обновление таблиц
}// класс для работы с бд
