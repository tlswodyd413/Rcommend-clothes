package jymw.project.recommendclothes.main;

public class WeatherInfoItem {
    public String thoroughfare;
    public String weather;
    public String tempMIn;
    public String tempNow;
    public String tempMax;
    public String evaluation;
    public String wind;
    public String dust;
    public String humidity;
    public String time;
    public String probability;

    public WeatherInfoItem(String thoroughfare, String weather, String tempMIn, String tempNow, String tempMax,
                           String evaluation, String wind, String dust, String humidity) {
        this.thoroughfare = thoroughfare;
        this.weather = weather;
        this.tempMIn = tempMIn;
        this.tempNow = tempNow;
        this.tempMax = tempMax;
        this.evaluation = evaluation;
        this.wind = wind;
        this.dust = dust;
        this.humidity = humidity;
    }

    public WeatherInfoItem(String time, String probability, String weather, String tempNow) {
        this.time = time;
        this.probability = probability;
        this.weather = weather;
        this.tempNow = tempNow;
    }
}