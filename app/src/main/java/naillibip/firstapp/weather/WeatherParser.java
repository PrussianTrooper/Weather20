package naillibip.firstapp.weather;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import naillibip.firstapp.weather.event.UpdateRecyclerList;
import naillibip.firstapp.weather.rest.models.WeatherModel;

public class WeatherParser {

    private static final String LOG_TAG = "WeatherParser";

    public static void parseDataToCard(CityCard cityCard, WeatherModel weatherModel, Context context) {
        Log.d(LOG_TAG, "starting parsing for " + cityCard.getCityName() + " with retrofit");
        assert weatherModel != null;
        cityCard.setTemp(weatherModel.main.temp);
        cityCard.setTempMax(weatherModel.main.tempMax);
        cityCard.setTempMin(weatherModel.main.tempMin);
        cityCard.setFeelsTemp(weatherModel.main.feelsLike);
        cityCard.setIcon(getWeatherIcon(
                weatherModel.weather.get(0).id,
                weatherModel.sys.sunrise,
                weatherModel.sys.sunset,
                context));
        cityCard.setHumidity(weatherModel.main.humidity);
        cityCard.setPressure(weatherModel.main.pressure);
        cityCard.setCityName(weatherModel.name);
        cityCard.setCountry(weatherModel.sys.country);
        cityCard.setDescription(weatherModel.weather.get(0).description);
        cityCard.setUpdateOn(getUpdateTime(weatherModel.dt));
        cityCard.setWind(weatherModel.wind.speed);
        EventBus.getBus().post(new UpdateRecyclerList(cityCard.getPosition()));
    }

    public static void parseDataToCardAndShowTempScreen(CityCard cityCard, WeatherModel weatherModel, Context context) {
        Log.d(LOG_TAG, "starting parsing for " + cityCard.getCityName() + " with retrofit");
        assert weatherModel != null;
        cityCard.setTemp(weatherModel.main.temp);
        cityCard.setTempMax(weatherModel.main.tempMax);
        cityCard.setTempMin(weatherModel.main.tempMin);
        cityCard.setFeelsTemp(weatherModel.main.feelsLike);
        cityCard.setIcon(getWeatherIcon(
                weatherModel.weather.get(0).id,
                weatherModel.sys.sunrise,
                weatherModel.sys.sunset,
                context));
        cityCard.setHumidity(weatherModel.main.humidity);
        cityCard.setPressure(weatherModel.main.pressure);
        cityCard.setCityName(weatherModel.name);
        cityCard.setCountry(weatherModel.sys.country);
        cityCard.setDescription(weatherModel.weather.get(0).description);
        cityCard.setUpdateOn(getUpdateTime(weatherModel.dt));
        cityCard.setWind(weatherModel.wind.speed);
        EventBus.getBus().post(new UpdateRecyclerList(cityCard.getPosition()));
    }

    private static String getUpdateTime(long dt) {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(new Date(dt * 1000));
    }

    private static String getWeatherIcon(int actualId, long sunrise, long sunset, Context context) {
        int id = actualId / 100;
        String icon = "";

        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = context.getString(R.string.weather_sunny);
            } else {
                icon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2: {
                    icon = context.getString(R.string.weather_thunder);
                    break;
                }
                case 3: {
                    icon = context.getString(R.string.weather_drizzle);
                    break;
                }
                case 5: {
                    icon = context.getString(R.string.weather_rainy);
                    break;
                }
                case 6: {
                    icon = context.getString(R.string.weather_snowy);
                    break;
                }
                case 7: {
                    icon = context.getString(R.string.weather_foggy);
                    break;
                }
                case 8: {
                    icon = context.getString(R.string.weather_cloudy);
                    break;
                }
            }
        }
        return icon;
    }
}
