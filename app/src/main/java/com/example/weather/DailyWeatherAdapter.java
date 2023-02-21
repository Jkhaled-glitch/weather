package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DailyWeatherAdapter extends ArrayAdapter<Weather> {
    private List<Weather> weatherList;
    Context context;
    public DailyWeatherAdapter(@NonNull Context context, @NonNull List<Weather> weatherList) {
        super(context,0, weatherList);
        this.context=context;
        this.weatherList=weatherList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_forecast,parent,false);
        TextView itemDate = convertView.findViewById(R.id.itemDate);
        TextView itemTemp = convertView.findViewById(R.id.itemTemp);
        ImageView itemIcon = convertView.findViewById(R.id.itemIcon);
        Weather weather = weatherList.get(position);
        itemTemp.setText(weather.getTemp()+" °C");
        Ion.with(context)
                .load("http://api.openweathermap.org/img/w/"+weather.getIcon()+".png")
                .intoImageView(itemIcon);
        Date date= new Date(weather.getDate()*1000);
        DateFormat dateFormat =new SimpleDateFormat("EEE,MMM  HH:mm", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(weather.getTimeZone()));
        itemDate.setText(dateFormat.format(date));


        return convertView;
    }
}

