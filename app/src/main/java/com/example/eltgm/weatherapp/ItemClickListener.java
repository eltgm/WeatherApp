package com.example.eltgm.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static android.support.v4.content.ContextCompat.startActivity;

public class ItemClickListener implements View.OnClickListener {
    private final int dayNum;
    Context mContext;
    String cityName;

    public ItemClickListener(Context mContext, int dayNum, String cityName) {
        this.mContext = mContext;
        this.dayNum = dayNum;
        this.cityName = cityName.toLowerCase();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, Day.class);
        intent.putExtra("dayNum",dayNum);
        intent.putExtra("cityName",cityName);

        Bundle bundle = null;
        startActivity(mContext,intent,bundle);
    }
}
