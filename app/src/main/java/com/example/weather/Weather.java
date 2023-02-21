package com.example.weather;

public class Weather{
    private long date;
    private String timeZone;
    private Double temp;
    private String icon;

    public Weather(long date,String timeZone, Double temp, String icon) {
        this.date = date;
        this.temp = temp;
        this.icon = icon;
        this.timeZone=timeZone;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
