package com.example.weather;



import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Implementation of App Widget functionality.
 */

public class NewAppWidget extends AppWidgetProvider {

    public static final String API_KEY="cff42b35ce9e1ad989fc956cf2010514";
    public static CharSequence widgetTexttemp = "20 °C";
    public static CharSequence widgetTextcity= "London, GB";

    private static final String ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //api openweathermap
        String city=citySaved(context);

        FusedLocationProviderClient fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(context, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    double lat=addresses.get(0).getLatitude();
                                    double lon=addresses.get(0).getLongitude();
                                    String urlAPI="http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&units=metric&appid="+API_KEY;


                                    Ion.with(context)
                                            .load(urlAPI)
                                            .asJsonObject()
                                            .setCallback(new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject result) {
                                                    // do stuff with the result
                                                    int cod = result.get("cod").getAsInt();
                                                    if(!(e!=null) && cod==200) {

                                                        //convert JSON response to java
                                                        JsonObject main = result.get("main").getAsJsonObject();
                                                        Double temp = main.get("temp").getAsDouble();
                                                        widgetTexttemp = temp+" °C";

                                                        String name = result.get("name").getAsString();
                                                        JsonObject sys = result.get("sys").getAsJsonObject();
                                                        String country = sys.get("country").getAsString();
                                                        widgetTextcity= name + ", " + country;

                                                        JsonArray weather =result.get("weather").getAsJsonArray();
                                                        String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();
                                                        //print the image
                                                        //loadIcon(icon);
                                                        //save the data
                                                        // Construct the RemoteViews object
                                                        final   RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                                                        views.setTextViewText(R.id.tvTemp_widget, widgetTexttemp);
                                                        views.setTextViewText(R.id.tvCity_widget, widgetTextcity);


                                                        String iconUrl = "https://api.openweathermap.org/img/w/"+icon+".png";
                                                        try {
                                                            Bitmap bitmap =
                                                                    Glide.with(context)
                                                                            .asBitmap()
                                                                            .load(iconUrl)
                                                                            .submit(512, 512)
                                                                            .get();

                                                            views.setImageViewBitmap(R.id.iconWeather_widget, bitmap);
                                                        }catch(Exception exception){
                                                            //handle exceptions (on n'affiche rien
                                                        }





                                                        // Create an Intent to launch ExampleActivity
                                                        Intent intent = new Intent(context, NewAppWidget.class);
                                                        intent.setAction(ACTION_SIMPLEAPPWIDGET);

                                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                                                /* context = */ context,
                                                                /* requestCode = */ 0,
                                                                /* intent = */ intent,
                                                                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                                        );

                                                        // Get the layout for the widget and attach an on-click listener
                                                        views.setOnClickPendingIntent(R.id.btn_widget, pendingIntent);

                                                        // Tell the AppWidgetManager to perform an update on the current app widget.
                                                        appWidgetManager.updateAppWidget(appWidgetId, views);

                                                        // Instruct the widget manager to update the widget
                                                        appWidgetManager.updateAppWidget(appWidgetId, views);

                                                    }
                                                }


                                            });





                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Ion.with(context)
                                            .load("http://api.openweathermap.org/data/2.5/weather?q="+city+"&&units=metric&appid="+API_KEY)
                                            .asJsonObject()
                                            .setCallback(new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject result) {
                                                    // do stuff with the result
                                                    int cod = result.get("cod").getAsInt();
                                                    if(!(e!=null) && cod==200) {

                                                        //convert JSON response to java
                                                        JsonObject main = result.get("main").getAsJsonObject();
                                                        Double temp = main.get("temp").getAsDouble();
                                                        widgetTexttemp = temp+" °C";

                                                        String name = result.get("name").getAsString();
                                                        JsonObject sys = result.get("sys").getAsJsonObject();
                                                        String country = sys.get("country").getAsString();
                                                        widgetTextcity= name + ", " + country;

                                                        JsonArray weather =result.get("weather").getAsJsonArray();
                                                        String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();
                                                        //print the image
                                                        //loadIcon(icon);
                                                        //save the data
                                                        // Construct the RemoteViews object
                                                        final   RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                                                        views.setTextViewText(R.id.tvTemp_widget, widgetTexttemp);
                                                        views.setTextViewText(R.id.tvCity_widget, widgetTextcity);


                                                        String iconUrl = "https://api.openweathermap.org/img/w/"+icon+".png";
                                                        try {
                                                            Bitmap bitmap =
                                                                    Glide.with(context)
                                                                            .asBitmap()
                                                                            .load(iconUrl)
                                                                            .submit(512, 512)
                                                                            .get();

                                                            views.setImageViewBitmap(R.id.iconWeather_widget, bitmap);
                                                        }catch(Exception exception){
                                                            //handle exceptions (on n'affiche rien
                                                        }





                                                        // Create an Intent to launch ExampleActivity
                                                        Intent intent = new Intent(context, NewAppWidget.class);
                                                        intent.setAction(ACTION_SIMPLEAPPWIDGET);

                                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                                                /* context = */ context,
                                                                /* requestCode = */ 0,
                                                                /* intent = */ intent,
                                                                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                                        );

                                                        // Get the layout for the widget and attach an on-click listener
                                                        views.setOnClickPendingIntent(R.id.btn_widget, pendingIntent);

                                                        // Tell the AppWidgetManager to perform an update on the current app widget.
                                                        appWidgetManager.updateAppWidget(appWidgetId, views);

                                                        // Instruct the widget manager to update the widget
                                                        appWidgetManager.updateAppWidget(appWidgetId, views);

                                                    }
                                                }


                                            });
                                }
                            }

                        }
                    });

        }
        else
        {
            Ion.with(context)
                    .load("http://api.openweathermap.org/data/2.5/weather?q="+city+"&&units=metric&appid="+API_KEY)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result
                            int cod = result.get("cod").getAsInt();
                            if(!(e!=null) && cod==200) {

                                //convert JSON response to java
                                JsonObject main = result.get("main").getAsJsonObject();
                                Double temp = main.get("temp").getAsDouble();
                                widgetTexttemp = temp+" °C";

                                String name = result.get("name").getAsString();
                                JsonObject sys = result.get("sys").getAsJsonObject();
                                String country = sys.get("country").getAsString();
                                widgetTextcity= name + ", " + country;

                                JsonArray weather =result.get("weather").getAsJsonArray();
                                String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();
                                //print the image
                                //loadIcon(icon);
                                //save the data
                                // Construct the RemoteViews object
                                final   RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                                views.setTextViewText(R.id.tvTemp_widget, widgetTexttemp);
                                views.setTextViewText(R.id.tvCity_widget, widgetTextcity);


                                String iconUrl = "https://api.openweathermap.org/img/w/"+icon+".png";
                                try {
                                    Bitmap bitmap =
                                            Glide.with(context)
                                                    .asBitmap()
                                                    .load(iconUrl)
                                                    .submit(512, 512)
                                                    .get();

                                    views.setImageViewBitmap(R.id.iconWeather_widget, bitmap);
                                }catch(Exception exception){
                                    //handle exceptions (on n'affiche rien
                                }





                                // Create an Intent to launch ExampleActivity
                                Intent intent = new Intent(context, NewAppWidget.class);
                                intent.setAction(ACTION_SIMPLEAPPWIDGET);

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                        /* context = */ context,
                                        /* requestCode = */ 0,
                                        /* intent = */ intent,
                                        /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                );

                                // Get the layout for the widget and attach an on-click listener
                                views.setOnClickPendingIntent(R.id.btn_widget, pendingIntent);

                                // Tell the AppWidgetManager to perform an update on the current app widget.
                                appWidgetManager.updateAppWidget(appWidgetId, views);

                                // Instruct the widget manager to update the widget
                                appWidgetManager.updateAppWidget(appWidgetId, views);

                            }
                        }


                    });


        }

    }



    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_SIMPLEAPPWIDGET.equals(intent.getAction())) {
            //api openweathermap

            String city=citySaved(context);
            FusedLocationProviderClient fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context);
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        double lat=addresses.get(0).getLatitude();
                                        double lon=addresses.get(0).getLongitude();
                                        String urlAPI="http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&units=metric&appid="+API_KEY;
                                        Ion.with(context)
                                                .load(urlAPI)
                                                .asJsonObject()
                                                .setCallback(new FutureCallback<JsonObject>() {
                                                    @Override
                                                    public void onCompleted(Exception e, JsonObject result) {
                                                        // do stuff with the result
                                                        int cod = result.get("cod").getAsInt();
                                                        if (!(e != null) && cod == 200) {
                                                            //convert JSON response to java
                                                            JsonObject main = result.get("main").getAsJsonObject();
                                                            Double temp = main.get("temp").getAsDouble();

                                                            widgetTexttemp = temp + " °C";
                                                            String name = result.get("name").getAsString();
                                                            JsonObject sys = result.get("sys").getAsJsonObject();
                                                            String country = sys.get("country").getAsString();
                                                            widgetTextcity = name + ", " + country;

                                                            JsonArray weather = result.get("weather").getAsJsonArray();
                                                            String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();

                                                            // Construct the RemoteViews object
                                                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                                                            views.setTextViewText(R.id.tvTemp_widget, widgetTexttemp);
                                                            views.setTextViewText(R.id.tvCity_widget, widgetTextcity);
                                                            String iconUrl = "https://api.openweathermap.org/img/w/" + icon + ".png";
                                                            // Use Glide to download and display the image asynchronously
                                                            try {
                                                                Toast.makeText(context, "Up To Date", Toast.LENGTH_SHORT).show();
                                                                Bitmap bitmap =
                                                                        Glide.with(context)
                                                                                .asBitmap()
                                                                                .load(iconUrl)
                                                                                .submit(512, 512)
                                                                                .get();

                                                                views.setImageViewBitmap(R.id.iconWeather_widget, bitmap);
                                                            } catch (Exception exception) {
                                                                //handle exceptions
                                                            }
                                                            ComponentName appWidget = new ComponentName(context, NewAppWidget.class);
                                                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                                                            // Instruct the widget manager to update the widget
                                                            appWidgetManager.updateAppWidget(appWidget, views);


                                                        }
                                                    }


                                                });
                                    } catch (IOException e) {
                                        e.printStackTrace();


                                        Ion.with(context)
                                                .load("http://api.openweathermap.org/data/2.5/weather?q=" +city + "&&units=metric&appid=" + API_KEY)
                                                .asJsonObject()
                                                .setCallback(new FutureCallback<JsonObject>() {
                                                    @Override
                                                    public void onCompleted(Exception e, JsonObject result) {
                                                        // do stuff with the result
                                                        int cod = result.get("cod").getAsInt();
                                                        if (!(e != null) && cod == 200) {
                                                            //convert JSON response to java
                                                            JsonObject main = result.get("main").getAsJsonObject();
                                                            Double temp = main.get("temp").getAsDouble();

                                                            widgetTexttemp = temp + " °C";
                                                            String name = result.get("name").getAsString();
                                                            JsonObject sys = result.get("sys").getAsJsonObject();
                                                            String country = sys.get("country").getAsString();
                                                            widgetTextcity = name + ", " + country;

                                                            JsonArray weather = result.get("weather").getAsJsonArray();
                                                            String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();

                                                            // Construct the RemoteViews object
                                                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                                                            views.setTextViewText(R.id.tvTemp_widget, widgetTexttemp);
                                                            views.setTextViewText(R.id.tvCity_widget, widgetTextcity);
                                                            String iconUrl = "https://api.openweathermap.org/img/w/" + icon + ".png";
                                                            // Use Glide to download and display the image asynchronously
                                                            try {
                                                                Toast.makeText(context, "Up To Date", Toast.LENGTH_SHORT).show();
                                                                Bitmap bitmap =
                                                                        Glide.with(context)
                                                                                .asBitmap()
                                                                                .load(iconUrl)
                                                                                .submit(512, 512)
                                                                                .get();

                                                                views.setImageViewBitmap(R.id.iconWeather_widget, bitmap);
                                                            } catch (Exception exception) {
                                                                //handle exceptions
                                                            }
                                                            ComponentName appWidget = new ComponentName(context, NewAppWidget.class);
                                                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                                                            // Instruct the widget manager to update the widget
                                                            appWidgetManager.updateAppWidget(appWidget, views);


                                                        }
                                                    }


                                                });
                                    }
                                }
                            }
                        });
            }


        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    private static final String citySaved(Context context) {
        // Gets the data repository in write mode
        CityDbHelper dbHelper = new CityDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();



        //Check if db null
        String[] selectionArgs = {"1"};
        Cursor cursor = db.query(
                "city",   // The table to query
                null,             // The array of columns to return (pass null to get all)
                "id = ?",              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        String City="";
        int i=1;
        while(cursor.moveToNext() && i==1){
            City= cursor.getString(
                    cursor.getColumnIndexOrThrow("city"));
            i++;
        }
        cursor.close();
        return City;

    }



}