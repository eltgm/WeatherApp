package com.example.eltgm.weatherapp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class WeatherDay {
    private Weather[] day;

    WeatherDay(Weather[] days) {
        this.day = days;
    }

    Weather[] getDay() {
        return day;
    }

    WeatherDay(String tempJSON, String humJSON, String windJSON, String presJSON, String descrJSON, String city_name){
        JSONParser parser = new JSONParser();
        Object JSONobj = null;
        Object JSONobj2 = null;
        Object JSONobj3 = null;
        Object JSONobj4 = null;
        Object JSONobj5 = null;
        try {
            JSONobj = parser.parse(tempJSON);
            JSONobj2 = parser.parse(humJSON);
            JSONobj3 = parser.parse(windJSON);
            JSONobj4 = parser.parse(presJSON);
            JSONobj5 = parser.parse(descrJSON);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject tempObj = (JSONObject) JSONobj;
        JSONObject humObj = (JSONObject) JSONobj2;
        JSONObject windObj = (JSONObject) JSONobj3;
        JSONObject presObj = (JSONObject) JSONobj4;
        JSONObject descrObj = (JSONObject) JSONobj5;

            int count = 0;
            while (!tempObj.containsKey(count+"temp"))
                count+=3;

            int temp = 0;
            Weather[] weath = new Weather[tempObj.size() - 1];
            while (tempObj.containsKey(count+"temp")){
                long tmp = Long.parseLong(tempObj.get(count+"temp").toString());
                long hum =  Long.parseLong(humObj.get(count+"hum").toString());
                double wind = Double.parseDouble(windObj.get(count+"wind").toString());
                double pres = Double.parseDouble(presObj.get(count+"pres").toString());
                String descr = String.valueOf(descrObj.get(count+"descr"));
                long day = Long.parseLong(tempObj.get("day").toString());
                weath[temp] = new Weather(tmp,hum,wind,pres,descr,city_name,day);
                temp++;
                count+=3;
            }
        this.day = weath;
    }
}//класс,хранящий погоду на день
