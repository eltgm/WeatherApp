package com.example.eltgm.weatherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    String[] cityNames;
    Context context;

        public static class ViewHolder extends RecyclerView.ViewHolder{

            public TextView cityName;

            public ViewHolder(View itemView) {
                super(itemView);

                cityName = (TextView) itemView.findViewById(R.id.cityName);
            }
        }

    public HistoryAdapter(String[] cityNames, Context context) {
        this.cityNames = cityNames;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View cityName = inflater.inflate(R.layout.historyitem, parent, false);

        return new ViewHolder(cityName);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String cityName = cityNames[position];

        TextView name = holder.cityName;
        name.setText(cityName);
        name.setOnClickListener(new HistoryItemListener(context, cityName));
    }


    @Override
    public int getItemCount() {
        return cityNames.length;
    }
}
