package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY="cff42b35ce9e1ad989fc956cf2010514";
    Button btnSearch;
    EditText etCityName;
    ImageView iconWeather;
    TextView tvTemp,tvCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearch= findViewById(R.id.btnSearch);
        etCityName= findViewById(R.id.etCityName);
        iconWeather= findViewById(R.id.iconWeather);
        tvTemp= findViewById(R.id.tvTemp);
        tvCity= findViewById(R.id.tvCity);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city =etCityName.getText().toString();
                if(city.isEmpty())
                    etCityName.setError("Enter City Name");
                else{
                    // TODO : load weather by city name
                    loadWeatherByCityName(city);
                }
            }
        });
    }
    private void loadWeatherByCityName(String city){
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
                                tvTemp.setText(temp + " °C");

                                String name = result.get("name").getAsString();
                                JsonObject sys = result.get("sys").getAsJsonObject();
                                String country = sys.get("country").getAsString();
                                tvCity.setText(name + ", " + country);

                                JsonArray weather =result.get("weather").getAsJsonArray();
                                String icon = weather.get(0).getAsJsonObject().get("icon").getAsString();
                                //print the image
                                loadIcon(icon);
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
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                //.animateLoad(spinAnimation)
                //.animateIn(fadeInAnimation)
                .load("http://api.openweathermap.org/img/w/"+icon+".png");
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