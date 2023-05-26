package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY="cff42b35ce9e1ad989fc956cf2010514";
    public static final String API_KEY2="462f445106adc1d21494341838c10019";

    Button btnSearch;
    EditText etCityName;
    ImageView iconWeather;
    TextView tvTemp,tvCity,tvDate,tvTime,tvWeather_status,tvDescription,tvTemp_max_min,tvHumidity,tvWind;
    ListView dailyWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearch= findViewById(R.id.btnSearch);
        etCityName= findViewById(R.id.etCityName);
        iconWeather= findViewById(R.id.iconWeather);
        tvTemp= findViewById(R.id.tvTemp);
        tvCity= findViewById(R.id.tvCity);
        dailyWeather= findViewById(R.id.dailyWeather);
        tvDate=findViewById(R.id.date);
        tvTime=findViewById(R.id.time);
        tvWeather_status=findViewById(R.id.weather_status);
        tvDescription=findViewById(R.id.description);
        tvTemp_max_min=findViewById(R.id.max_min_temp);
        tvHumidity=findViewById(R.id.humidity);
        tvWind=findViewById(R.id.wind);

        getCityByLastLocation();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city =etCityName.getText().toString();
                if(city.isEmpty())
                    etCityName.setError("Enter City Name");
                else{
                    // TODO : load weather by city name
                    loadWeather(city);
                }
            }
        });

    }

    private void loadWeather(String city){

        Ion.with(getApplicationContext())
                .load("http://api.openweathermap.org/data/2.5/weather?q="+city+"&&units=metric&appid="+API_KEY)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if(e!=null) {
                            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        else{
                            int cod = result.get("cod").getAsInt();
                            if(cod==200) {
                                //convert JSON response to java
                                JsonObject main = result.get("main").getAsJsonObject();
                                Double temp = main.get("temp").getAsDouble();


                                String name = result.get("name").getAsString();
                                JsonObject sys = result.get("sys").getAsJsonObject();
                                String country = sys.get("country").getAsString();


                                JsonArray weather =result.get("weather").getAsJsonArray();
                                String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();

                                String description=weather.get(0).getAsJsonObject().get("description").getAsString();
                                String weather_status=weather.get(0).getAsJsonObject().get("main").getAsString();
                                String temp_min=main.get("temp_min").getAsString();
                                String temp_max=main.get("temp_max").getAsString();
                                double humidity=main.get("humidity").getAsDouble();
                                double speed=result.get("wind").getAsJsonObject().get("speed").getAsDouble();
                                long dt=result.get("dt").getAsLong();
                                int timezone=result.get("timezone").getAsInt();

                                //date and hour
                                Date dtDate= new Date(dt*1000);
                                DateFormat dateFormat =new SimpleDateFormat("EEE,dd MMM  ", Locale.ENGLISH);
                                DateFormat hourFormat =new SimpleDateFormat("hh:mm", Locale.ENGLISH);
                                hourFormat.setTimeZone(TimeZone.getTimeZone(Integer.toString(timezone)));
                                dateFormat.setTimeZone(TimeZone.getTimeZone(Integer.toString(timezone)));
                                String date=dateFormat.format(dtDate);
                                String hour = hourFormat.format(dtDate);

                                //update views
                                tvCity.setText(name + ", " + country);
                                tvDate.setText(date);
                                tvTime.setText(hour);
                                tvWeather_status.setText(weather_status);
                                tvDescription.setText(description);
                                tvTemp_max_min.setText(temp_min+"|"+temp_max);
                                tvTime.setText(hour);
                                tvTemp.setText(temp + " °C");
                                tvHumidity.setText("Humidity: "+humidity);
                                tvWind.setText("Wind: "+speed+"km/h");


                                //print the image
                                loadIcon(icon);
                                //save the data
                                saveDataToSQLite(name);

                                //Handle
                                JsonObject coord = result.get("coord").getAsJsonObject();
                                double lon = coord.get("lon").getAsDouble();
                                double lat = coord.get("lat").getAsDouble();
                                loadDailyForecast(lon, lat);
                            }
                            else{
                                String message=result.get("message").getAsString();
                                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }

    private void loadWeather(double lat,double lon){
        String urlAPI="http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&units=metric&appid="+API_KEY;
        Ion.with(getApplicationContext())
                .load(urlAPI)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if(e!=null) {
                            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        else{
                            int cod = result.get("cod").getAsInt();
                            if(cod==200) {
                                //convert JSON response to java
                                JsonObject main = result.get("main").getAsJsonObject();
                                Double temp = main.get("temp").getAsDouble();


                                String name = result.get("name").getAsString();
                                Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
                                JsonObject sys = result.get("sys").getAsJsonObject();
                                String country = sys.get("country").getAsString();

                                JsonArray weather =result.get("weather").getAsJsonArray();
                                String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();

                                String description=weather.get(0).getAsJsonObject().get("description").getAsString();
                                String weather_status=weather.get(0).getAsJsonObject().get("main").getAsString();
                                String temp_min=main.get("temp_min").getAsString();
                                String temp_max=main.get("temp_max").getAsString();
                                double humidity=main.get("humidity").getAsDouble();
                                double speed=result.get("wind").getAsJsonObject().get("speed").getAsDouble();
                                long dt=result.get("dt").getAsLong();
                                int timezone=result.get("timezone").getAsInt();

                                //date and hour
                                Date dtDate= new Date(dt*1000);
                                DateFormat dateFormat =new SimpleDateFormat("EEE,dd MMM  ", Locale.ENGLISH);
                                DateFormat hourFormat =new SimpleDateFormat("hh:mm", Locale.ENGLISH);
                                hourFormat.setTimeZone(TimeZone.getTimeZone(Integer.toString(timezone)));
                                dateFormat.setTimeZone(TimeZone.getTimeZone(Integer.toString(timezone)));
                                String date=dateFormat.format(dtDate);
                                String hour = hourFormat.format(dtDate);

                                //update views
                                tvCity.setText(name + ", " + country);
                                tvDate.setText(date);
                                tvTime.setText(hour);
                                tvWeather_status.setText(weather_status);
                                tvDescription.setText(description);
                                tvTemp_max_min.setText(temp_min+"|"+temp_max);
                                tvTime.setText(hour);
                                tvTemp.setText(temp + " °C");
                                tvHumidity.setText("Humidity: "+humidity);
                                tvWind.setText("Wind: "+speed+"km/h");
                                //print the image
                                loadIcon(icon);
                                //save the data
                                saveDataToSQLite(name);

                                //Handle
                                JsonObject coord = result.get("coord").getAsJsonObject();
                                double lon = coord.get("lon").getAsDouble();
                                double lat = coord.get("lat").getAsDouble();
                                loadDailyForecast(lon, lat);
                            }
                            else{
                                String message=result.get("message").getAsString();
                                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }
    private void loadIcon(String icon){
        Ion.with(iconWeather)
                .load("http://api.openweathermap.org/img/w/"+icon+".png");
    }
    private void loadDailyForecast(double lon, double lat){
      //  http://api.openweathermap.org/data/2.5/forecast?lat=36.648404&lon=10.293903&lang=fr&exclude=hourly,minutely,current&appid=462f445106adc1d21494341838c10019
        String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&cnt=40&units=metric&appid="+API_KEY2;
//      String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&cnt=8&exclude=hourly,minutely,current&units=metric&appid="+API_KEY;
        Ion.with(getApplicationContext())
                .load(apiUrl)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {

                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        // do stuff with the result or error
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            int cod = result.get("cod").getAsInt();
                               if(cod==200) {
                                   int cnt = result.get("cnt").getAsInt();
                                   //Result
                                   List<Weather> weatherList = new ArrayList<>();
                                   String timeZone = result.get("city").getAsJsonObject().get("timezone").getAsString();
                                   JsonArray list = result.get("list").getAsJsonArray();
                                   for (int i = 7; i < cnt; i+=8) {
                                       Long date = list.get(i).getAsJsonObject().get("dt").getAsLong();
                                       Double temp = list.get(i).getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsDouble();
                                       String icon = list.get(i).getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                                       weatherList.add(new Weather(date, timeZone, temp, icon));
                                   }

                                   DailyWeatherAdapter dailyWeatherAdapter = new DailyWeatherAdapter(getApplicationContext(), weatherList);
                                   dailyWeather.setAdapter(dailyWeatherAdapter);
                               }
                               else{
                                   String message=result.get("message").getAsString();
                                   Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                               }

                        }
                    }
                });


    }

    private void getCityByLastLocation() {

        FusedLocationProviderClient fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getApplicationContext());

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(getApplicationContext(),"march",Toast.LENGTH_LONG).show();
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    //double lat=0;


                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                                    double lat=addresses.get(0).getLatitude();
                                    double lon=addresses.get(0).getLongitude();

                                    loadWeather(lat,lon);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

        }
        else
        {

            askPermission();
        }

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {android.Manifest.permission.ACCESS_FINE_LOCATION},100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==100){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCityByLastLocation();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void saveDataToSQLite(String city) {

        CityDbHelper dbHelper = new CityDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Gets the data repository in read mode
        SQLiteDatabase db2 = dbHelper.getReadableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("id", "1");
        values.put("city", city);

        //Check if db null

        String[] selectionArgs = {"1"};
        Cursor cursor = db2.query(
                "city",   // The table to query
                null,             // The array of columns to return (pass null to get all)
                "id = ?",              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        int x= 0;
        while (cursor.moveToNext()) {
            x++;
            int count = db.update("city", values, "id like ?", selectionArgs);
        }
        if(x==0){
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert("city", null, values);

        }
    }


}
/*
{
        "coord":{"lon":-0.1257,"lat":51.5085},
        "weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],
        "base":"stations",
        "main":{"temp":281.55,"feels_like":278.3,"temp_min":280.85,"temp_max":282.48,"pressure":1035,"humidity":73},
        "visibility":10000,
        "wind":{"speed":6.17,"deg":260},
        "clouds":{"all":100},
        "dt":1676044400,
        "sys":{"type":2,"id":2075535,"country":"GB","sunrise":1676013877,"sunset":1676048698},
        "timezone":0,
        "id":2643743,
        "name":"London",
        "cod":200
      }

 */