package naillibip.firstapp.weather.rest;

import naillibip.firstapp.weather.rest.models.WeatherModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface IOpenWeather {

    @GET("data/2.5/weather?")
    Observable<WeatherModel> getCurrentWeatherDataWithRx(
            @Query("q") String city,
            @Query("APPID") String app_id,
            @Query("units") String units,
            @Query("lang") String lang);

    @GET("data/2.5/weather?")
    Call<WeatherModel> getCurrentWeatherData(
            @Query("q") String city,
            @Query("APPID") String app_id,
            @Query("units") String units,
            @Query("lang") String lang);

    @GET("data/2.5/weather?")
    Call<WeatherModel> getCurrentWeatherDataByGeo(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("APPID") String app_id,
            @Query("units") String units,
            @Query("lang") String lang);

    @GET("data/2.5/forecast?")
    Call<WeatherModel> getFiveDaysWeatherData(
            @Query("q") String city,
            @Query("APPID") String app_id,
            @Query("units") String units,
            @Query("lang") String lang);
}
