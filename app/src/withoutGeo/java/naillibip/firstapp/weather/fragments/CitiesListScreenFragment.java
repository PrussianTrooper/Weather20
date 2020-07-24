package naillibip.firstapp.weather.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import naillibip.firstapp.weather.App;
import naillibip.firstapp.weather.CitiesHistorySource;
import naillibip.firstapp.weather.CityCard;
import naillibip.firstapp.weather.CityPreference;
import naillibip.firstapp.weather.EventBus;
import naillibip.firstapp.weather.R;
import naillibip.firstapp.weather.WeatherDataLoader;
import naillibip.firstapp.weather.activity.TempScreenActivity;
import naillibip.firstapp.weather.adapters.CitiesListRecyclerViewAdapter;
import naillibip.firstapp.weather.callBackInterfaces.IAdapterCallbacks;
import naillibip.firstapp.weather.dao.ICitiesDao;
import naillibip.firstapp.weather.event.AddCityToListEvent;
import naillibip.firstapp.weather.event.UpdateRecyclerList;

public class CitiesListScreenFragment extends Fragment implements IAdapterCallbacks {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.cities_list_progress_bar)
    ProgressBar progressBar;

    private boolean isTempScreenExists;
    private Unbinder unbinder;
    private CitiesListRecyclerViewAdapter adapter;
    private List<CityCard> cityCardsFromSQL;
    private String cityName, units;
    private ArrayList<CityCard> cityCards;
    private CityPreference cityPreference;
    private CitiesHistorySource citiesHistorySource;

    private static final String LOG_TAG = "CityList fragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cities_list, container, false);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, v);
        return v;
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
        super.onStop();
        EventBus.getBus().unregister(this);
        cityPreference.setList(cityCards);
    }

    @Subscribe
    public void onStopParsingDataToCardEvent(UpdateRecyclerList event) {
        adapter.notifyItemChanged(event.getPosition());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSQLServices();
        initPreferences();
        initList();
        renderWeather();
        setSwipeListener();
        if (savedInstanceState == null) startTempScreenFragment(cityPreference.getCity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            boolean permissionsGranted = (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if (permissionsGranted) Objects.requireNonNull(getActivity()).recreate();
        }
    }

    private void initSQLServices() {
        ICitiesDao citiesDao = App.getInstance().getCitiesDao();
        citiesHistorySource = new CitiesHistorySource(citiesDao);
        cityCardsFromSQL = citiesHistorySource.getCityCardList();
    }

    private void setSwipeListener() {
        swipeRefreshLayout.setOnRefreshListener(this::renderWeather);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTempScreenExists = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        cityName = cityPreference.getCity();
        if (isTempScreenExists) {
            showTempScreen(getCityCardByItsName(cityName));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("city", cityName);
        if (cityPreference != null) saveList();
        super.onSaveInstanceState(outState);
    }

    private void initPreferences() {
        cityPreference = new CityPreference(Objects.requireNonNull(getActivity()));
        cityCards = cityPreference.getList();
        units = cityPreference.getUnits();
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CitiesListRecyclerViewAdapter(cityCards, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        final int[] dragStartFromPosition = {-1};
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            int dragFromPosition = -1;
            int dragToPosition = -1;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (dragFromPosition == -1) {
                    dragStartFromPosition[0] = viewHolder.getAdapterPosition();
                }

                dragFromPosition = viewHolder.getAdapterPosition();
                dragToPosition = target.getAdapterPosition();
                Collections.swap(cityCards, dragFromPosition, dragToPosition);
                dragFromPosition = dragToPosition;
                adapter.notifyItemMoved(dragFromPosition, dragToPosition);
                dragToPosition = -1;
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void startTempScreenFragment(String city) {
        cityPreference.setCity(city);
        showTempScreen(getCityCardByItsName(city));
    }

    private void showTempScreen(CityCard cityCard) {
        if (cityCard != null) {
            String city = cityCard.getCityName();
            String units = cityPreference.getUnits();
            int pressure = cityPreference.getPressure();
            if (isTempScreenExists) {
                TempScreenFragment detail = (TempScreenFragment) Objects.requireNonNull(
                        getFragmentManager()).findFragmentById(R.id.temp_screen);
                if (detail == null || !detail.getCityName().equals(city)) {
                    detail = TempScreenFragment.newInstance(cityCard, units, pressure);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.temp_screen, detail);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.addToBackStack("Some_key");
                    fragmentTransaction.commit();
                }
            } else {
                Intent intent = new Intent();
                intent.setClass(Objects.requireNonNull(getActivity()), TempScreenActivity.class);
                intent.putExtra("index", city);
                intent.putExtra("units", units);
                intent.putExtra("pressure", pressure);
                intent.putExtra("cityCard", cityCard);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAdapterUpdate() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void saveList() {
        cityPreference.setList(cityCards);
    }

    private void deleteCityFromList() {
        adapter.removeItem();
        saveList();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_city_btn) {
            showInputDialog(getContext(), getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInputDialog(Context context, Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(activity));
        builder.setCancelable(true);

        builder.setTitle(R.string.add_city);
        @SuppressLint("InflateParams") View alertBoxView = getLayoutInflater().inflate(R.layout.add_city_alert_box, null);
        TextInputEditText input = alertBoxView.findViewById(R.id.alert_box_editText);
        ChipGroup chipGroup = alertBoxView.findViewById(R.id.alert_box_chipGroup);
        builder.setView(alertBoxView);
        cityCardsFromSQL = citiesHistorySource.getCityCardList();
        if (cityCardsFromSQL != null) {
            for (CityCard c : cityCardsFromSQL) {
                Log.w("CitiesListFragment", c.cityName + " " + c.numberOfSearches);
                final Chip chip = new Chip(context);
                String text = c.cityName;
                chip.setText(text);
                chipGroup.addView(chip);
                chip.setOnClickListener(view -> input.setText(c.cityName));
            }
        }
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            if (!Objects.requireNonNull(input.getText()).toString().isEmpty()) {
                String city = input.getText().toString();
                CityCard cityCard = new CityCard(city);
                if (!cityCards.contains(cityCard))
                    WeatherDataLoader.getCurrentDataAndAddCity(cityCard, context, units);
            }
        });
        builder.setNegativeButton(R.string.cancel,
                (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void renderWeather() {
        for (CityCard c : cityCards) {
            c.setPosition(cityCards.indexOf(c));
            Log.d(LOG_TAG, "update city weather: " + c.getCityName());
            WeatherDataLoader.getCurrentData(c, Objects.requireNonNull(getContext()), units);
        }
    }

    @Subscribe
    public void addCityToList(AddCityToListEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        CityCard cityCard = event.getCityCard();
        if (cityCard.getCityName() != null && !cityCards.contains(cityCard)) {
            Log.w("CitiesListFragment", "adding city");
            if (!cityCardsFromSQL.contains(cityCard)) citiesHistorySource.addCity(cityCard);
            else {
                for (CityCard c : cityCardsFromSQL) {
                    if (c.equals(cityCard)) {
                        c.numberOfSearches++;
                        citiesHistorySource.updateCity(c);
                    }
                }
            }
            adapter.addItem(cityCard);
            cityCard.setPosition(cityCards.indexOf(cityCard));
            if (cityPreference != null) saveList();
            recyclerView.scrollToPosition(0);
            Snackbar.make(recyclerView, R.string.city_added, Snackbar.LENGTH_LONG)
                    .setAction(R.string.cancel, view -> deleteCityFromList()).show();
            adapter.notifyDataSetChanged();
        }
    }

    private CityCard getCityCardByItsName(String city) {
        for (CityCard c : cityCards) {
            if (c.getCityName().equals(city)) return c;
        }
        return null;
    }

    final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            WeatherDataLoader.getCurrentDataByGeo(latitude, longitude, Objects.requireNonNull(getContext()), units);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


}

