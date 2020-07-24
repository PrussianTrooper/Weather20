package naillibip.firstapp.weather.fragments;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.squareup.otto.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import naillibip.firstapp.weather.CityCard;
import naillibip.firstapp.weather.CityPreference;
import naillibip.firstapp.weather.EventBus;
import naillibip.firstapp.weather.R;
import naillibip.firstapp.weather.WeatherDataLoader;
import naillibip.firstapp.weather.event.SetValuesOnFragmentEvent;

public class TempScreenFragment extends Fragment {

    @BindView(R.id.temperatureValue)
    TextView tempTodayTopTextView;
    @BindView(R.id.temperatureFeelsLikeValue)
    TextView tempFeelsLikeTextView;
    @BindView(R.id.tempValueMax)
    TextView tempMaxTextView;
    @BindView(R.id.tempValueMin)
    TextView tempMinTextView;
    @BindView(R.id.humidityValue)
    TextView humidityTextView;
    @BindView(R.id.pressureValue)
    TextView pressureTextView;
    @BindView(R.id.windValue)
    TextView windTextView;
    @BindView(R.id.overcastValue)
    TextView overcastTextView;
    @BindView(R.id.cityTextView)
    TextView cityTextView;
    @BindView(R.id.todayDate)
    TextView todayDateTextView;
    @BindView(R.id.weatherIcon)
    TextView weatherIconTextView;
    //ImageButton
    @BindView(R.id.updateBtn)
    ImageButton updateBtn;
    @BindView(R.id.back_arrow)
    ImageButton backArrowBtn;
    @BindView(R.id.backgroundImageTempScreen)
    ImageView backgroundImage;
    @BindView(R.id.progressBarTempFragment)
    ProgressBar progressBar;

    private int humidityValue, pressure, pressureValue, windValue;
    private double tempToday, tempFeelsLike, tempMax, tempMin;
    private String cityName, overcastValue, country, updateOn, icon, units;
    private final String HUMIDITY_VALUE_KEY = "HUMIDITY_VALUE_KEY", OVERCAST_VALUE_KEY = "overcast",
            TEMP_TODAY_KEY = "TEMP_TODAY_KEY", CITY_NAME_KEY = "CITY_NAME_KEY", COUNTRY_KEY = "COUNTRY_KEY",
            TEMP_FEELS_LIKE_KEY = "TEMP_FEELS_LIKE_KEY", TEMP_MAX_KEY = "TEMP_MAX_KEY", TEMP_MIN_KEY = "TEMP_MIN_KEY",
            PRESSURE_KEY = "PRESSURE_KEY", WIND_KEY = "WIND_KEY", ICON_KEY = "ICON_KEY";

    private Unbinder unbinder;
    private CityCard cityCard;
    private CityPreference cityPreference;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //Name
            cityName = savedInstanceState.getString(CITY_NAME_KEY);
            country = savedInstanceState.getString(COUNTRY_KEY);
            //Temp
            tempToday = savedInstanceState.getDouble(TEMP_TODAY_KEY);
            tempFeelsLike = savedInstanceState.getDouble(TEMP_FEELS_LIKE_KEY);
            tempMax = savedInstanceState.getDouble(TEMP_MAX_KEY);
            tempMin = savedInstanceState.getDouble(TEMP_MIN_KEY);
            //Descriptions
            humidityValue = savedInstanceState.getInt(HUMIDITY_VALUE_KEY);
            overcastValue = savedInstanceState.getString(OVERCAST_VALUE_KEY);
            pressureValue = savedInstanceState.getInt(PRESSURE_KEY);
            windValue = savedInstanceState.getInt(WIND_KEY);
            icon = savedInstanceState.getString(ICON_KEY);
            setValues(new SetValuesOnFragmentEvent());
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temp_screen, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getBus().unregister(this);
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFont();
        onUpdateBtnClicked();
        showBackButton();
        onBackArrowBtnClicked();

        cityPreference = new CityPreference(Objects.requireNonNull(getActivity()));
        cityCard = cityPreference.getCityCard(cityPreference.getCity());
        units = cityPreference.getUnits();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        WeatherDataLoader.getCurrentDataAndSetValues(cityCard, Objects.requireNonNull(getContext()), units);
    }

    private void initFont() {
        Typeface weatherFont = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/weathericons.ttf");
        weatherIconTextView.setTypeface(weatherFont);
    }

    private void showBackButton() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            backArrowBtn.setVisibility(View.INVISIBLE);
        else backArrowBtn.setVisibility(View.VISIBLE);
    }

    private void onBackArrowBtnClicked() {
        backArrowBtn.setOnClickListener(view -> {
            if (getActivity() != null) getActivity().finish();
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(CITY_NAME_KEY, cityName);
        savedInstanceState.putString(COUNTRY_KEY, country);
        savedInstanceState.putDouble(TEMP_TODAY_KEY, tempToday);
        savedInstanceState.putDouble(TEMP_FEELS_LIKE_KEY, tempFeelsLike);
        savedInstanceState.putDouble(TEMP_MAX_KEY, tempMax);
        savedInstanceState.putDouble(TEMP_MIN_KEY, tempMin);
        savedInstanceState.putInt(HUMIDITY_VALUE_KEY, humidityValue);
        savedInstanceState.putInt(WIND_KEY, windValue);
        savedInstanceState.putInt(PRESSURE_KEY, pressureValue);
        savedInstanceState.putString(OVERCAST_VALUE_KEY, overcastValue);
        savedInstanceState.putString(ICON_KEY, icon);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Subscribe
    public void setValues(SetValuesOnFragmentEvent event) {
        updateWeatherOnScreen();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        String degree = chooseDegree();
        //city + country
        String cityText = cityName + ", " + country;
        cityTextView.setText(cityText);
        //date of update
        String updatedText = getString(R.string.last_update) + " " + updateOn;
        todayDateTextView.setText(updatedText);
        //temp today
        String tempTodayString = String.valueOf(Math.round(tempToday)).concat(degree);
        tempTodayTopTextView.setText(tempTodayString);
        //temp feels like
        String tempFeelsLikeString = getString(R.string.feels_like) + " " + Math.round(tempFeelsLike) + degree;
        tempFeelsLikeTextView.setText(tempFeelsLikeString);
        // temp min
        String tempMinString = "Min: " + String.valueOf(tempMin).concat(degree);
        tempMinTextView.setText(tempMinString);
        //temp max
        String tempMaxString = "Max: " + String.valueOf(tempMax).concat(degree);
        tempMaxTextView.setText(tempMaxString);
        //Descriptions
        //overcast
        overcastTextView.setText(overcastValue);
        //humidity
        humidityTextView.setText(String.valueOf(humidityValue).concat("%"));
        //pressure
        pressureTextView.setText(choosePressure());
        //wind
        windTextView.setText(String.valueOf(windValue).concat(getString(R.string.ms)));
        //icon
        weatherIconTextView.setText(icon);
    }

    private void updateWeatherOnScreen(){
        if (getArguments()!= null) {
            units = getArguments().getString("units");
            pressure = getArguments().getInt("pressure");
            if (cityCard != null) {
                cityName = cityCard.getCityName();
                country = cityCard.getCountry();
                updateOn = cityCard.getUpdateOn();
                tempToday = cityCard.getTemp();
                tempFeelsLike = cityCard.getFeelsTemp();
                tempMin = cityCard.getTempMin();
                tempMax = cityCard.getTempMax();
                overcastValue = cityCard.getDescription();
                humidityValue = cityCard.getHumidity();
                pressureValue = cityCard.getPressure();
                windValue = cityCard.getWind();
                icon = cityCard.getIcon();
            }
        }
    }

    private void onUpdateBtnClicked() {
        updateBtn.setOnClickListener(view -> {
            Toast.makeText(getContext(), getString(R.string.updating), Toast.LENGTH_SHORT).show();
            WeatherDataLoader.getCurrentDataAndSetValues(cityCard, Objects.requireNonNull(getContext()), units);
        });
    }

    static TempScreenFragment newInstance(CityCard cityCard, String units, int pressure) {
        Bundle args = new Bundle();
        TempScreenFragment fragment = new TempScreenFragment();
        args.putString("index", cityCard.getCityName());
        args.putString("units", units);
        args.putInt("pressure", pressure);
        args.putSerializable("cityCard", cityCard);
        fragment.setArguments(args);
        return fragment;
    }

    String getCityName() {
        cityName = Objects.requireNonNull(getArguments()).getString("index");
        try {
            return cityName;
        } catch (Exception e) {
            return "";
        }
    }

    private String chooseDegree(){
        if (units.equals("metric")) return getString(R.string.celsius);
        else return getString(R.string.fahrenheit);
    }

    private String choosePressure() {
        if (pressure == 0)
            return String.valueOf(Math.round(pressureValue / 1.33322387415)).concat(getString(R.string.mmHg));
        else return pressureValue + getString(R.string.hPa);
    }
}

